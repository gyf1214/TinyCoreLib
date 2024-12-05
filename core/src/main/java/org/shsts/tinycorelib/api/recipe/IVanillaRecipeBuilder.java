package org.shsts.tinycorelib.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IVanillaRecipeBuilder<R extends IRecipe<?>, S extends IVanillaRecipeBuilder<R, S>>
    extends IBuilder<FinishedRecipe, IRecipeType<S>, S> {
}
