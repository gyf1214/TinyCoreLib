package org.shsts.tinycorelib.content.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.registrate.IEntryHandler;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.content.registrate.builder.ItemBuilder;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;
import org.shsts.tinycorelib.content.registrate.handler.TintHandler;
import org.shsts.tinycorelib.content.tracking.TrackedObjects;
import org.shsts.tinycorelib.content.tracking.TrackedType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Registrate implements IRegistrate {
    public final String modid;

    private final Map<ResourceKey<?>, EntryHandler<?>> entryHandlers = new HashMap<>();

    public final EntryHandler<Item> itemHandler;
    public final EntryHandler<Block> blockHandler;
    public final TintHandler tintHandler;

    private final TrackedObjects trackedObjects;

    public Registrate(String modid) {
        this.modid = modid;

        this.itemHandler = createEntryHandler(ForgeRegistries.ITEMS);
        this.blockHandler = createEntryHandler(ForgeRegistries.BLOCKS);

        this.tintHandler = new TintHandler();

        this.trackedObjects = new TrackedObjects();
    }

    @SuppressWarnings("unchecked")
    private <T extends IForgeRegistryEntry<T>> EntryHandler<T> createEntryHandler(
        IForgeRegistry<T> registry) {
        return (EntryHandler<T>) entryHandlers.computeIfAbsent(registry.getRegistryKey(),
            $ -> new EntryHandler<>(this, registry));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IForgeRegistryEntry<T>> IEntryHandler<T> entryHandler(ResourceKey<T> key) {
        return (EntryHandler<T>) entryHandlers.get(key);
    }

    @Override
    public void register(IEventBus modEventBus) {
        for (var handler : entryHandlers.values()) {
            handler.addListener(modEventBus);
        }
    }

    @Override
    public void registerClient(IEventBus modEventBus) {
        modEventBus.addListener(tintHandler::onRegisterBlockColors);
        modEventBus.addListener(tintHandler::onRegisterItemColors);
    }

    @Override
    public <U extends Item, P> IItemBuilder<U, P> item(P parent, String id,
        Function<Item.Properties, U> factory) {
        return (new ItemBuilder<>(this, parent, id, factory))
            .onCreateObject(this::trackItem);
    }

    @Override
    public <P> IItemBuilder<Item, P> item(P parent, String id) {
        return item(parent, id, Item::new);
    }

    public void trackTranslation(String key) {
        trackedObjects.put(TrackedType.LANG, key, key);
    }

    public void trackBlock(Block block) {
        var loc = block.getRegistryName();
        assert loc != null;
        trackedObjects.put(TrackedType.BLOCK, block, loc.toString());
        trackTranslation(block.getDescriptionId());
    }

    public void trackItem(Item item) {
        var loc = item.getRegistryName();
        assert loc != null;
        trackedObjects.put(TrackedType.ITEM, item, loc.toString());
        trackTranslation(item.getDescriptionId());
    }

    public <V> Map<V, String> getTracked(TrackedType<V> type) {
        return trackedObjects.getObjects(type);
    }
}
