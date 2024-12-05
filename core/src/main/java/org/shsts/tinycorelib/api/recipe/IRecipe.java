package org.shsts.tinycorelib.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.core.ILoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipe<C> extends ILoc {
    boolean matches(C container, Level world);
}
