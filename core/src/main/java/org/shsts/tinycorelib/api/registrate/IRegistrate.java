package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.registrate.builder.IBlockBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRegistryBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistrate {
    <V extends IForgeRegistryEntry<V>> IEntryHandler<V> getHandler(
        IForgeRegistry<V> registry);

    <V extends IForgeRegistryEntry<V>> IEntryHandler<V> getHandler(
        ResourceKey<Registry<V>> key, Class<?> entryClass);

    <T> ICapability<T> getCapability(CapabilityToken<T> token);

    IBlockEntityType getBlockEntityType(ResourceLocation loc);

    IBlockEntityType getBlockEntityType(String id);

    void register(IEventBus modEventBus);

    void registerClient(IEventBus modEventBus);

    <V extends IForgeRegistryEntry<V>, P> IRegistryBuilder<V, P> registry(
        P parent, String id, Class<V> entryClass);

    <V extends IForgeRegistryEntry<V>, P> IRegistryBuilder<V, P> genericRegistry(
        P parent, String id, Class<?> entryClass);

    default <V extends IForgeRegistryEntry<V>> IRegistryBuilder<V, IRegistrate> registry(
        String id, Class<V> entryClass) {
        return registry(this, id, entryClass);
    }

    default <V extends IForgeRegistryEntry<V>> IRegistryBuilder<V, IRegistrate> genericRegistry(
        String id, Class<?> entryClass) {
        return genericRegistry(this, id, entryClass);
    }

    <U extends Block, P> IBlockBuilder<U, P> block(P parent, String id,
        Function<BlockBehaviour.Properties, U> factory);

    default <U extends Block> IBlockBuilder<U, IRegistrate> block(String id,
        Function<BlockBehaviour.Properties, U> factory) {
        return block(this, id, factory);
    }

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

    <P> IBlockEntityTypeBuilder<P> blockEntityType(P parent, String id);

    default IBlockEntityTypeBuilder<IRegistrate> blockEntityType(String id) {
        return blockEntityType(this, id);
    }

    <T> ICapability<T> capability(Class<T> clazz, CapabilityToken<T> token);

    <T extends IForgeRegistryEntry<T>, U extends T> IEntry<U> registryEntry(
        IEntryHandler<T> handler, String id, Supplier<U> factory);

    <A> IEntry<IEvent<A>> event(String id);

    <A, R> IEntry<IReturnEvent<A, R>> returnEvent(String id, R defaultResult);
}
