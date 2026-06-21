package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RendererHandler {
    private final List<Consumer<EntityRenderersEvent.RegisterRenderers>> callbacks = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    public <T extends BlockEntity> void setBlockEntityRenderer(
        Supplier<BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
        callbacks.add(event -> event.registerBlockEntityRenderer(type.get(), provider));
    }

    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (var entry : callbacks) {
            entry.accept(event);
        }
        // release references
        callbacks.clear();
    }
}
