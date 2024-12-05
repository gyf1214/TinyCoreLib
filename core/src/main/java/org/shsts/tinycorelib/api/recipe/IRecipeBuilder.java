package org.shsts.tinycorelib.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeBuilder<R extends IRecipe<?>, S extends IRecipeBuilder<R, S>>
    extends IBuilder<R, IRecipeType<S>, S> {
}
