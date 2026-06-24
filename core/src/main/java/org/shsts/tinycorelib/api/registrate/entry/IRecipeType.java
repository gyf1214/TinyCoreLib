package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeType<R extends IRecipe<?>> extends IEntry<RecipeType<?>> {
    RecipeSerializer<?> getSerializer();

    Class<R> recipeClass();
}
