package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RendererHandler {
    private record BlockEntityEntry<T extends BlockEntity>(
        BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider) {
        public void register(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(type, provider);
        }
    }

    private final List<BlockEntityEntry<?>> blockEntityRenderers = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    public <T extends BlockEntity> void setBlockEntityRenderer(
        BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider) {
        blockEntityRenderers.add(new BlockEntityEntry<>(type, provider));
    }

    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (var entry : blockEntityRenderers) {
            entry.register(event);
        }
    }
}
