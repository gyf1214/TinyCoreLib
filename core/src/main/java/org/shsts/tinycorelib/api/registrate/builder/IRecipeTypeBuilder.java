package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipeSerializer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeTypeBuilder<R extends IRecipe<?>, B extends IRecipeBuilder<R, B>, P> extends
    IRecipeTypeBuilderBase<R, B, P, IRecipeTypeBuilder<R, B, P>> {
    IRecipeTypeBuilder<R, B, P> serializer(IRecipeSerializer<R, B> value);
}
