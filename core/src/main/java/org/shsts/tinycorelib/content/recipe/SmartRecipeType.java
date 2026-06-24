package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeType<C, R extends IRecipe<C>> implements RecipeType<SmartRecipe<C, R>> {
    private final ResourceLocation loc;
    public final Class<R> recipeClass;

    public SmartRecipeType(ResourceLocation loc, Class<R> recipeClass) {
        this.loc = loc;
        this.recipeClass = recipeClass;
    }

    @Override
    public String toString() {
        return loc.toString();
    }
}
