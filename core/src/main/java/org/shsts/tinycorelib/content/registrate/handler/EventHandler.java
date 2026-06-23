package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.Nullable;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntity;
import org.shsts.tinycorelib.content.gui.SmartMenuType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EventHandler<E> {
    private final List<Consumer<E>> callbacks = new ArrayList<>();

    public void addCallback(Consumer<E> cb) {
        callbacks.add(cb);
    }

    public void onEvent(E event) {
        for (var cb : callbacks) {
            cb.accept(event);
        }
        callbacks.clear();
    }

    public static class MenuScreen extends EventHandler<RegisterMenuScreensEvent> {
        @OnlyIn(Dist.CLIENT)
        public <M extends MenuBase> void setMenuScreen(SmartMenuType<M> type,
            IMenuScreenFactory<M, ?> factory) {
            addCallback(event -> event.register(type, factory));
        }
    }

    public static class Renderer extends EventHandler<EntityRenderersEvent.RegisterRenderers> {
        @OnlyIn(Dist.CLIENT)
        public <T extends BlockEntity> void setBlockEntityRenderer(
            Supplier<BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
            addCallback(event -> event.registerBlockEntityRenderer(type.get(), provider));
        }
    }

    public static class Capability extends EventHandler<RegisterCapabilitiesEvent> {
        public <T, BE extends SmartBlockEntity> void register(
            BlockEntityType<BE> type, ICapability<T> capability) {
            register(type, capability, be -> be.getCapability(capability));
        }

        public <T, BE extends SmartBlockEntity> void register(
            BlockEntityType<BE> type, ICapability<T> capability,
            Function<? super BE, T> provider) {
            addCallback(event -> registerBlockEntity(event, capability.get(), type, provider));
        }

        private static <T, C extends @Nullable Object, BE extends SmartBlockEntity> void registerBlockEntity(
            RegisterCapabilitiesEvent event, BlockCapability<T, C> capability,
            BlockEntityType<BE> type, Function<? super BE, T> provider) {
            event.registerBlockEntity(capability, type, (be, $) -> provider.apply(be));
        }
    }
}
