package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.core.ISelf;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistrate extends ISelf<IRegistrate> {
    <T extends IForgeRegistryEntry<T>> IEntryHandler<T> entryHandler(ResourceKey<T> key);

    void register(IEventBus modEventBus);

    void registerClient(IEventBus modEventBus);

    <U extends Item> IItemBuilder<U, IRegistrate> item(String id,
        Function<Item.Properties, U> factory);

    IItemBuilder<Item, IRegistrate> item(String id);
}
