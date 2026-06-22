package org.shsts.tinycorelib.content.registrate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.api.registrate.builder.IBlockBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IMenuBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRegistryBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;
import org.shsts.tinycorelib.content.blockentity.Event;
import org.shsts.tinycorelib.content.blockentity.ReturnEvent;
import org.shsts.tinycorelib.content.registrate.builder.BlockBuilder;
import org.shsts.tinycorelib.content.registrate.builder.BlockEntityTypeBuilder;
import org.shsts.tinycorelib.content.registrate.builder.ItemBuilder;
import org.shsts.tinycorelib.content.registrate.builder.MenuBuilder;
import org.shsts.tinycorelib.content.registrate.builder.RecipeTypeBuilder;
import org.shsts.tinycorelib.content.registrate.builder.RegistryBuilderWrapper;
import org.shsts.tinycorelib.content.registrate.builder.SimpleEntryBuilder;
import org.shsts.tinycorelib.content.registrate.entry.CapabilityEntry;
import org.shsts.tinycorelib.content.registrate.handler.BlockEntityTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.CapabilityHandler;
import org.shsts.tinycorelib.content.registrate.handler.CreativeTabHandler;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;
import org.shsts.tinycorelib.content.registrate.handler.EventHandler;
import org.shsts.tinycorelib.content.registrate.handler.MenuTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.RecipeTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.RegistryHandler;
import org.shsts.tinycorelib.content.registrate.handler.TintHandler;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedObjects;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_REGISTRY_KEY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Registrate implements IRegistrate {
    public final String modid;

    private final Map<ResourceLocation, EntryHandler<?>> entryHandlers = new HashMap<>();

    // registry
    public final RegistryHandler registryHandler;

    // special forge registry
    public final BlockEntityTypeHandler blockEntityTypeHandler;
    public final MenuTypeHandler menuTypeHandler;
    public final RecipeTypeHandler recipeTypeHandler;

    // others
    public final CapabilityHandler capabilityHandler;
    public final CreativeTabHandler creativeTabHandler;

    // client only
    public final TintHandler tintHandler;
    public final EventHandler.MenuScreen menuScreenHandler;
    public final EventHandler.Renderer rendererHandler;

    private final TrackedObjects trackedObjects;

    @Nullable
    private IChannel defaultChannel = null;

    public Registrate(String modid) {
        this.modid = modid;

        this.registryHandler = new RegistryHandler(this);
        this.blockEntityTypeHandler = createEntryHandler(BlockEntityTypeHandler::new);
        this.menuTypeHandler = createEntryHandler(MenuTypeHandler::new);
        this.recipeTypeHandler = new RecipeTypeHandler(this);
        this.capabilityHandler = new CapabilityHandler(this);
        this.creativeTabHandler = new CreativeTabHandler();

        this.tintHandler = new TintHandler();
        this.menuScreenHandler = new EventHandler.MenuScreen();
        this.rendererHandler = new EventHandler.Renderer();

        this.trackedObjects = new TrackedObjects();
    }

    public <V> void addEntryHandler(ResourceLocation loc,
        EntryHandler<V> handler) {
        entryHandlers.put(loc, handler);
    }

    private <H extends EntryHandler<?>> H createEntryHandler(Function<Registrate, H> factory) {
        var handler = factory.apply(this);
        entryHandlers.put(handler.registryKey().location(), handler);
        return handler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> EntryHandler<V> getHandler(ResourceKey<? extends Registry<V>> key,
        Registry<V> registry, Class<V> entryClass) {
        return (EntryHandler<V>) entryHandlers.computeIfAbsent(key.location(),
            $ -> new EntryHandler<>(this, key, registry));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> EntryHandler<V> getHandler(
        ResourceKey<? extends Registry<V>> key, Class<?> entryClass) {
        return (EntryHandler<V>) entryHandlers.computeIfAbsent(key.location(),
            $ -> new EntryHandler<>(this, key, () -> registryHandler.getRegistry(key)));
    }

    @Override
    public IBlockEntityType getBlockEntityType(ResourceLocation loc) {
        return blockEntityTypeHandler.getTypeEntry(loc);
    }

    @Override
    public IBlockEntityType getBlockEntityType(String id) {
        return blockEntityTypeHandler.getTypeEntry(id);
    }

    @Override
    public IMenuType getMenuType(ResourceLocation loc) {
        return menuTypeHandler.getTypeEntry(loc);
    }

    @Override
    public IMenuType getMenuType(String id) {
        return menuTypeHandler.getTypeEntry(id);
    }

    @Override
    public IRecipeType<?> getRecipeType(ResourceLocation loc) {
        return recipeTypeHandler.getRecipeType(loc);
    }

    @Override
    public IRecipeType<?> getRecipeType(String id) {
        return recipeTypeHandler.getRecipeType(id);
    }

    @Override
    public void register(IEventBus modEventBus) {
        modEventBus.addListener(registryHandler::onNewRegistry);
        for (var handler : entryHandlers.values()) {
            handler.addListener(modEventBus);
        }
        recipeTypeHandler.addListener(modEventBus);
        modEventBus.addListener(capabilityHandler::onRegisterEvent);
        modEventBus.addListener(creativeTabHandler::onRegisterCreativeTabs);
    }

    @Override
    public void registerClient(IEventBus modEventBus) {
        modEventBus.addListener(tintHandler::onRegisterBlockColors);
        modEventBus.addListener(tintHandler::onRegisterItemColors);
        modEventBus.addListener(rendererHandler::onEvent);
        modEventBus.addListener(menuScreenHandler::onEvent);
    }

    @Override
    public <V, P> IRegistryBuilder<V, P> registry(
        P parent, String id, Class<V> entryClass) {
        return new RegistryBuilderWrapper<>(this, parent, id, entryClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, P> IRegistryBuilder<V, P> genericRegistry(
        P parent, String id, Class<?> entryClass) {
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
    public <P> IBlockEntityTypeBuilder<P> blockEntityType(P parent, String id) {
        return new BlockEntityTypeBuilder<>(this, parent, id);
    }

    @Override
    public <M extends MenuBase, P> IMenuBuilder<M, P> menu(P parent, String id,
        Function<MenuBase.Properties, M> menuFactory) {
        var builder = new MenuBuilder<>(this, parent, id, menuFactory);
        return defaultChannel == null ? builder : builder.channel(defaultChannel);
    }

    @Override
    public IRegistrate setDefaultChannel(@Nullable IChannel value) {
        defaultChannel = value;
        return this;
    }

    @Override
    public <T> ICapability<T> capability(String id, Class<T> typeClass) {
        return new CapabilityEntry<>(modid, id, typeClass);
    }

    @Override
    public <T> ICapability<T> capability(
        BlockCapability<T, @org.jetbrains.annotations.Nullable Void> capability) {
        return new CapabilityEntry<>(capability);
    }

    @Override
    public <T, U extends T> IEntry<U> registryEntry(
        IEntryHandler<T> handler, String id, Supplier<U> factory) {
        return new SimpleEntryBuilder<>(this, (EntryHandler<T>) handler, this, id, factory)
            .register();
    }

    private EntryHandler<IEvent<?>> getEventHandler() {
        return getHandler(EVENT_REGISTRY_KEY, IEvent.class);
    }

    @Override
    public <A> IEntry<IEvent<A>> event(String id) {
        return registryEntry(getEventHandler(), id, Event::new);
    }

    @Override
    public <A, R> IEntry<IReturnEvent<A, R>> returnEvent(String id, R defaultResult) {
        return registryEntry(getEventHandler(), id,
            () -> new ReturnEvent<>(defaultResult));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends IRecipe<?>, P> IRecipeTypeBuilder<R, P> recipeType(P parent, String id) {
        return (IRecipeTypeBuilder<R, P>) new RecipeTypeBuilder(this, parent, id);
    }

    @Override
    public void trackLang(String key) {
        trackedObjects.put(TrackedType.LANG, key, key);
    }

    public void trackBlock(Block block) {
        var loc = BuiltInRegistries.BLOCK.getKey(block);
        trackedObjects.put(TrackedType.BLOCK, block, loc.toString());
        trackLang(block.getDescriptionId());
    }

    public void trackItem(Item item) {
        var loc = BuiltInRegistries.ITEM.getKey(item);
        trackedObjects.put(TrackedType.ITEM, item, loc.toString());
        trackLang(item.getDescriptionId());
    }

    public <V> Map<V, String> getTracked(TrackedType<V> type) {
        return trackedObjects.getObjects(type);
    }
}
