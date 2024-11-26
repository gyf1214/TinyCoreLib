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

    public Registrate(String modid) {
        this.modid = modid;

        this.itemHandler = createEntryHandler(ForgeRegistries.ITEMS);
        this.blockHandler = createEntryHandler(ForgeRegistries.BLOCKS);

        this.tintHandler = new TintHandler();
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
    public <U extends Item> IItemBuilder<U, IRegistrate> item(String id,
        Function<Item.Properties, U> factory) {
        return new ItemBuilder<>(this, this, id, factory);
    }

    @Override
    public IItemBuilder<Item, IRegistrate> item(String id) {
        return item(id, Item::new);
    }
}
