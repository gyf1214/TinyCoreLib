package org.shsts.tinycorelib.mixin;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import org.shsts.tinycorelib.content.gametest.DefaultGameTestTemplates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(GameTestRegistry.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class GameTestRegistryMixin {
    @Inject(method = "turnMethodIntoTestFunction", at = @At("RETURN"), cancellable = true)
    private static void applyDefaultTemplate(
        Method method,
        CallbackInfoReturnable<TestFunction> callback) {
        var testFunction = callback.getReturnValue();
        DefaultGameTestTemplates.apply(method, testFunction).ifPresent(callback::setReturnValue);
    }
}
