package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipeDataConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeType<B> extends IEntry<RecipeType<?>> {
    RecipeSerializer<?> getSerializer();

    B getBuilder(ResourceLocation loc);

    B recipe(IRecipeDataConsumer consumer, ResourceLocation loc);

    interface BuilderFactory<B> {
        B create(IRecipeType<B> parent, ResourceLocation loc);
    }
}
