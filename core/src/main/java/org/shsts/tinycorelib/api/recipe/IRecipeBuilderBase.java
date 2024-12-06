package org.shsts.tinycorelib.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

/**
 * Used to capture generic parameter.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeBuilderBase<R extends IRecipe<?>> {
}
