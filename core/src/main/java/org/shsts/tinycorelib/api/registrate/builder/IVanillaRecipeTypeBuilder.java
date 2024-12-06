package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeSerializer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IVanillaRecipeTypeBuilder<R extends IRecipe<?>,
    B extends IVanillaRecipeBuilder<R, B>, P> extends
    ILoc, IRecipeTypeBuilderBase<R, B, P, IVanillaRecipeTypeBuilder<R, B, P>> {
    IVanillaRecipeTypeBuilder<R, B, P> serializer(IVanillaRecipeSerializer<R> value);
}
