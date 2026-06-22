package org.shsts.tinycorelib.api.registrate.builder;

import com.mojang.serialization.MapCodec;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeTypeBuilder<R extends IRecipe<?>, P>
    extends IEntryBuilder<RecipeType<?>, RecipeType<?>, P, IRecipeTypeBuilder<R, P>> {
    IRecipeTypeBuilder<R, P> recipeClass(Class<R> clazz);

    IRecipeTypeBuilder<R, P> serializer(MapCodec<R> value);

    IRecipeType<R> register();
}
