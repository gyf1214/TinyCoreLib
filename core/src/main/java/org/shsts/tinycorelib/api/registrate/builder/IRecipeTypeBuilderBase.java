package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeTypeBuilderBase<R extends IRecipe<?>, B extends IRecipeBuilderBase<R>,
    P, S extends IRecipeTypeBuilderBase<R, B, P, S>>
    extends ILoc, IBuilder<RecipeType<?>, P, S> {
    S recipeClass(Class<R> clazz);

    S defaults(Transformer<B> trans);

    IRecipeType<B> register();
}
