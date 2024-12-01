package org.shsts.tinycorelib.content.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.registrate.IEntry;
import org.shsts.tinycorelib.api.registrate.IEntryHandler;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.api.registrate.builder.IBlockBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRegistryBuilder;
import org.shsts.tinycorelib.content.blockentity.Event;
import org.shsts.tinycorelib.content.blockentity.ReturnEvent;
import org.shsts.tinycorelib.content.registrate.builder.BlockBuilder;
import org.shsts.tinycorelib.content.registrate.builder.ItemBuilder;
import org.shsts.tinycorelib.content.registrate.builder.RegistryBuilderWrapper;
import org.shsts.tinycorelib.content.registrate.builder.SimpleEntryBuilder;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;
import org.shsts.tinycorelib.content.registrate.handler.RegistryHandler;
import org.shsts.tinycorelib.content.registrate.handler.RenderTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.TintHandler;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedObjects;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.shsts.tinycorelib.content.CoreContents.EVENT_REGISTRY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Registrate implements IRegistrate {
    public final String modid;

    private final Map<ResourceLocation, EntryHandler<?>> entryHandlers = new HashMap<>();

    // registry
    public final RegistryHandler registryHandler;

    // registry entries
    public final EntryHandler<Block> blockHandler;
    public final EntryHandler<Item> itemHandler;

    // client only
    public final RenderTypeHandler renderTypeHandler;
    public final TintHandler tintHandler;

    private final TrackedObjects trackedObjects;

    public Registrate(String modid) {
        this.modid = modid;

        this.registryHandler = new RegistryHandler();

        this.itemHandler = createEntryHandler(ForgeRegistries.ITEMS);
        this.blockHandler = createEntryHandler(ForgeRegistries.BLOCKS);

        this.renderTypeHandler = new RenderTypeHandler();
        this.tintHandler = new TintHandler();

        this.trackedObjects = new TrackedObjects();
    }

    @SuppressWarnings("unchecked")
    private <T extends IForgeRegistryEntry<T>> EntryHandler<T> createEntryHandler(
        IForgeRegistry<T> registry) {
        return (EntryHandler<T>) entryHandlers.computeIfAbsent(registry.getRegistryName(),
            $ -> new EntryHandler<>(this, registry));
    }

    public <T extends IForgeRegistryEntry<T>> void addEntryHandler(ResourceLocation loc,
        EntryHandler<T> handler) {
        entryHandlers.put(loc, handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IForgeRegistryEntry<T>> IEntryHandler<T> entryHandler(
        ResourceKey<Registry<T>> key, Class<?> entryClass) {
        return (EntryHandler<T>) entryHandlers.computeIfAbsent(key.location(),
            $ -> new EntryHandler<>(this, (Class<T>) entryClass,
                () -> registryHandler.getRegistry(key)));
    }

    @Override
    public void register(IEventBus modEventBus) {
        modEventBus.addListener(registryHandler::onNewRegistry);
        for (var handler : entryHandlers.values()) {
            handler.addListener(modEventBus);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(renderTypeHandler::onClientSetup);
    }

    @Override
    public void registerClient(IEventBus modEventBus) {
        modEventBus.addListener(tintHandler::onRegisterBlockColors);
        modEventBus.addListener(tintHandler::onRegisterItemColors);
        modEventBus.addListener(this::onClientSetup);
    }

    @Override
    public <V extends IForgeRegistryEntry<V>, P> IRegistryBuilder<V, P> registry(
        P parent, String id, Class<V> entryClass) {
        return new RegistryBuilderWrapper<>(this, parent, id, entryClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>, P> IRegistryBuilder<V, P> genericRegistry(P parent, String id,
        Class<?> entryClass) {
        return new RegistryBuilderWrapper<>(this, parent, id, (Class<V>) entryClass);
    }

    @Override
    public <U extends Block, P> IBlockBuilder<U, P> block(P parent, String id,
        Function<BlockBehaviour.Properties, U> factory) {
        return new BlockBuilder<>(this, parent, id, factory);
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

    @Override
    public <T extends IForgeRegistryEntry<T>, U extends T> IEntry<U> registryEntry(
        IEntryHandler<T> handler, String id, Supplier<U> factory) {
        return new SimpleEntryBuilder<>(this, (EntryHandler<T>) handler, this, id, factory)
            .register();
    }

    @Override
    public <A> IEntry<IEvent<A>> event(String id) {
        return registryEntry(EVENT_REGISTRY.getHandler(), id, Event::new);
    }

    @Override
    public <A, R> IEntry<IReturnEvent<A, R>> returnEvent(String id, R defaultResult) {
        return registryEntry(EVENT_REGISTRY.getHandler(), id,
            () -> new ReturnEvent<>(defaultResult));
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
