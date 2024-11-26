package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistrate {
    <T extends IForgeRegistryEntry<T>> IEntryHandler<T> entryHandler(ResourceKey<T> key);

    void register(IEventBus modEventBus);

    void registerClient(IEventBus modEventBus);

    <U extends Item, P> IItemBuilder<U, P> item(P parent, String id,
        Function<Item.Properties, U> factory);

    <P> IItemBuilder<Item, P> item(P parent, String id);

    default <U extends Item> IItemBuilder<U, IRegistrate> item(String id,
        Function<Item.Properties, U> factory) {
        return item(this, id, factory);
    }

    default IItemBuilder<Item, IRegistrate> item(String id) {
        return item(this, id);
    }
}
