package org.shsts.tinycorelib.content.registrate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeBuilder;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.api.registrate.builder.IBlockBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IMenuBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IRegistryBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IVanillaRecipeTypeBuilder;
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
import org.shsts.tinycorelib.content.registrate.builder.VanillaRecipeTypeBuilder;
import org.shsts.tinycorelib.content.registrate.entry.CapabilityEntry;
import org.shsts.tinycorelib.content.registrate.handler.BlockEntityTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.CapabilityHandler;
import org.shsts.tinycorelib.content.registrate.handler.DynamicHandler;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;
import org.shsts.tinycorelib.content.registrate.handler.MenuScreenHandler;
import org.shsts.tinycorelib.content.registrate.handler.MenuTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.RecipeTypeHandler;
import org.shsts.tinycorelib.content.registrate.handler.RegistryHandler;
import org.shsts.tinycorelib.content.registrate.handler.RenderTypeHandler;
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
    private final Map<ResourceLocation, DynamicHandler<?>> dynamicHandlers = new HashMap<>();

    // registry
    public final RegistryHandler registryHandler;

    // special forge registry
    public final BlockEntityTypeHandler blockEntityTypeHandler;
    public final MenuTypeHandler menuTypeHandler;
    public final RecipeTypeHandler recipeTypeHandler;

    // others
    public final CapabilityHandler capabilityHandler;

    // client only
    public final RenderTypeHandler renderTypeHandler;
    public final TintHandler tintHandler;
    public final MenuScreenHandler menuScreenHandler;

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
        this.renderTypeHandler = new RenderTypeHandler();
        this.tintHandler = new TintHandler();
        this.menuScreenHandler = new MenuScreenHandler();

        this.trackedObjects = new TrackedObjects();
    }

    public <V extends IForgeRegistryEntry<V>> void addEntryHandler(ResourceLocation loc,
        EntryHandler<V> handler) {
        entryHandlers.put(loc, handler);
    }

    private <H extends EntryHandler<?>> H createEntryHandler(Function<Registrate, H> factory) {
        var handler = factory.apply(this);
        entryHandlers.put(handler.getRegistry().getRegistryName(), handler);
        return handler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>> EntryHandler<V> getHandler(
        IForgeRegistry<V> registry) {
        return (EntryHandler<V>) entryHandlers.computeIfAbsent(registry.getRegistryName(),
            $ -> new EntryHandler<>(this, registry));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>> EntryHandler<V> getHandler(
        ResourceKey<Registry<V>> key, Class<?> entryClass) {
        return (EntryHandler<V>) entryHandlers.computeIfAbsent(key.location(),
            $ -> new EntryHandler<>(this, (Class<V>) entryClass,
                () -> registryHandler.getRegistry(key)));
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
    public <T> ICapability<T> getCapability(CapabilityToken<T> token) {
        return new CapabilityEntry<>(token);
    }

    @Override
    public <T> ICapability<T> getCapability(Capability<T> cap) {
        return new CapabilityEntry<>(cap);
    }

    @Override
    public <T extends IForgeRegistryEntry<T>> IRegistrate createDynamicHandler(
        IForgeRegistry<T> registry, Supplier<T> dummy) {
        var handler = new DynamicHandler<>(registry.getRegistrySuperType(), dummy);
        dynamicHandlers.put(registry.getRegistryName(), handler);
        return this;
    }

    @Override
    public void register(IEventBus modEventBus) {
        modEventBus.addListener(registryHandler::onNewRegistry);
        for (var handler : entryHandlers.values()) {
            handler.addListener(modEventBus);
        }
        for (var handler : dynamicHandlers.values()) {
            handler.addListener(modEventBus);
        }
        recipeTypeHandler.addListeners(modEventBus);
        modEventBus.addListener(capabilityHandler::onRegisterEvent);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(renderTypeHandler::onClientSetup);
        event.enqueueWork(menuScreenHandler::onClientSetup);
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
    public <V extends IForgeRegistryEntry<V>, P> IRegistryBuilder<V, P> genericRegistry(
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
    public <P> IMenuBuilder<P> menu(P parent, String id) {
        var builder = new MenuBuilder<>(this, parent, id);
        return defaultChannel == null ? builder : builder.channel(defaultChannel);
    }

    @Override
    public IRegistrate setDefaultChannel(@Nullable IChannel value) {
        defaultChannel = value;
        return this;
    }

    @Override
    public <T> ICapability<T> capability(Class<T> clazz, CapabilityToken<T> token) {
        return capabilityHandler.register(clazz, token);
    }

    @Override
    public <T extends IForgeRegistryEntry<T>, U extends T> IEntry<U> registryEntry(
        IEntryHandler<T> handler, String id, Supplier<U> factory) {
        return new SimpleEntryBuilder<>(this, (EntryHandler<T>) handler, this, id, factory)
            .register();
    }

    @Override
    public <T extends IForgeRegistryEntry<T>> ResourceKey<T> dynamicEntry(
        IForgeRegistry<T> registry, String id) {
        var loc = new ResourceLocation(modid, id);
        dynamicHandlers.get(registry.getRegistryName()).register(loc);
        return ResourceKey.create(registry.getRegistryKey(), loc);
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
    public <C, R extends IRecipe<C>, B extends IRecipeBuilder<R, B>,
        P> IRecipeTypeBuilder<R, B, P> recipeType(P parent, String id,
        IRecipeType.BuilderFactory<B> builderFactory) {
        return new RecipeTypeBuilder<>(this, parent, id, builderFactory);
    }

    @Override
    public <C, R extends IRecipe<C>, B extends IVanillaRecipeBuilder<R, B>,
        P> IVanillaRecipeTypeBuilder<R, B, P> vanillaRecipeType(P parent,
        String id, IRecipeType.BuilderFactory<B> builderFactory) {
        return new VanillaRecipeTypeBuilder<>(this, parent, id, builderFactory);
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
