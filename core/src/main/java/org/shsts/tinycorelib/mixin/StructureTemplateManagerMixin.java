package org.shsts.tinycorelib.mixin;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.gametest.GameTestHooks;
import org.shsts.tinycorelib.content.gametest.EmptyGameTestStructures;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StructureTemplateManager.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StructureTemplateManagerMixin {
    @Shadow
    @Final
    private HolderGetter<Block> blockLookup;

    @Inject(
        method = "tryLoad(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;",
        at = @At("HEAD"),
        cancellable = true)
    private void loadEmptyGameTestStructure(
        ResourceLocation id,
        CallbackInfoReturnable<Optional<StructureTemplate>> callback) {
        if (!GameTestHooks.isGametestServer()) {
            return;
        }

        var template = EmptyGameTestStructures.create(id, this.blockLookup);
        template.ifPresent(value -> callback.setReturnValue(Optional.of(value)));
    }
}
