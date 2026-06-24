package org.shsts.tinycorelib.datagen.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeFactory<R extends IRecipe<?>, B extends IBuilder<R, IRecipeFactory<R, B>, B>> {
    B recipe(String id);

    B recipe(ResourceLocation loc);
}
