package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeType<C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>>
    implements RecipeType<SmartRecipe<C, R>> {
    private final ResourceLocation loc;
    public final IRecipeType.BuilderFactory<B> builderFactory;
    public final String prefix;
    public final Transformer<B> defaults;
    public final Class<? extends R> recipeClass;

    public SmartRecipeType(ResourceLocation loc, IRecipeType.BuilderFactory<B> builderFactory,
        String prefix, Transformer<B> defaults, Class<? extends R> recipeClass) {
        this.loc = loc;
        this.builderFactory = builderFactory;
        this.prefix = prefix;
        this.defaults = defaults;
        this.recipeClass = recipeClass;
    }

    @Override
    public String toString() {
        return loc.toString();
    }
}
