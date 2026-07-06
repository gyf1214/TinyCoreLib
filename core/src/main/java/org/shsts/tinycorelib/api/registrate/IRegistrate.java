package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.builder.IBlockBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IMenuBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IPacketBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRegistryBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;

import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistrate {
    <U> IEntry<U> createEntry(ResourceLocation loc, U obj);

    <V> IEntryHandler<V> getHandler(ResourceKey<? extends Registry<V>> key, Registry<V> registry);

    <V> IEntryHandler<V> getHandler(ResourceKey<? extends Registry<V>> key);

    /**
     * Only use this when you are sure that the BlockEntityType is registered by this library.
     */
    IBlockEntityType getBlockEntityType(ResourceLocation loc);

    /**
     * Only use this when you are sure that the BlockEntityType is registered by this library.
     */
    IBlockEntityType getBlockEntityType(String id);

    /**
     * Only use this when you are sure that the MenuType is registered by this library.
     */
    IMenuType getMenuType(ResourceLocation loc);

    /**
     * Only use this when you are sure that the MenuType is registered by this library.
     */
    IMenuType getMenuType(String id);

    <R extends IRecipe<?>> IRecipeType<R> getRecipeType(ResourceLocation loc);

    <R extends IRecipe<?>> IRecipeType<R> getRecipeType(String id);

    <T> ICapability<T> getCapability(BlockCapability<T, ?> capability);

    <T> ICapability<T> getCapability(ResourceLocation loc, Class<T> typeClass, Class<?> ctxClass);

    <T> ICapability<T> getCapability(ResourceLocation loc, Class<T> typeClass);

    <T> IItemCapability<T> getItemCapability(ItemCapability<T, ?> capability);

    <T> IItemCapability<T> getItemCapability(ResourceLocation loc, Class<T> typeClass, Class<?> ctxClass);

    <T> IItemCapability<T> getItemCapability(ResourceLocation loc, Class<T> typeClass);

    void register(IEventBus modEventBus);

    void registerClient(IEventBus modEventBus);

    <V, P> IRegistryBuilder<V, P> registry(P parent, String id, Class<V> entryClass);

    <V, P> IRegistryBuilder<V, P> genericRegistry(P parent, String id, Class<?> entryClass);

    default <V> IRegistryBuilder<V, IRegistrate> registry(String id, Class<V> entryClass) {
        return registry(this, id, entryClass);
    }

    default <V> IRegistryBuilder<V, IRegistrate> genericRegistry(String id, Class<?> entryClass) {
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

    <M extends MenuBase, P> IMenuBuilder<M, P> menu(P parent, String id,
        Function<MenuBase.Properties, M> menuFactory);

    default <M extends MenuBase> IMenuBuilder<M, IRegistrate> menu(
        String id, Function<MenuBase.Properties, M> menuFactory) {
        return menu(this, id, menuFactory);
    }

    <T extends IPacket, P> IPacketBuilder<T, P> packet(P parent, String id, Supplier<T> constructor);

    default <T extends IPacket> IPacketBuilder<T, IRegistrate> packet(
        String id, Supplier<T> constructor) {
        return packet(this, id, constructor);
    }

    <T extends IPacket> IPacketType<T> menuSyncPacket(String id, Supplier<T> constructor);

    <T extends IPacket> IPacketType<T> menuEventPacket(String id, Supplier<T> constructor);

    <T> ICapability<T> capability(String id, Class<T> typeClass);

    <T> IItemCapability<T> itemCapability(String id, Class<T> typeClass);

    <T, U extends T> IEntry<U> registryEntry(IEntryHandler<T> handler, String id, Supplier<U> factory);

    <A> IEntry<IEvent<A>> event(String id);

    <A, R> IEntry<IReturnEvent<A, R>> returnEvent(String id, R defaultResult);

    <R extends IRecipe<?>, P> IRecipeTypeBuilder<R, P> recipeType(P parent, String id, Class<R> clazz);

    default <R extends IRecipe<?>> IRecipeTypeBuilder<R, IRegistrate> recipeType(String id, Class<R> clazz) {
        return recipeType(this, id, clazz);
    }

    void trackLang(String key);
}
