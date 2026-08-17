package org.shsts.tinycorelib;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.content.recipe.SmartRecipeManager;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLibClient {
    public static IRecipeManager clientRecipeManager() {
        var world = Minecraft.getInstance().level;
        assert world != null;
        return new SmartRecipeManager(world);
    }
}
