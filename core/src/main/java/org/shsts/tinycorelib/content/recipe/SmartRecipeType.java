package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeType<C, R extends IRecipe<C>, B>
    implements RecipeType<SmartRecipe<C, R>> {
    public final IRecipeType.BuilderFactory<B> builderFactory;
    public final String prefix;
    public final Transformer<B> defaults;
    public final Class<R> recipeClass;

    public SmartRecipeType(IRecipeType.BuilderFactory<B> builderFactory, String prefix,
        Transformer<B> defaults, Class<R> recipeClass) {
        this.builderFactory = builderFactory;
        this.prefix = prefix;
        this.defaults = defaults;
        this.recipeClass = recipeClass;
    }
}
