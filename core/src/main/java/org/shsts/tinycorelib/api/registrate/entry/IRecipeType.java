package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.recipe.IRecipeDataConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeType<B extends IRecipeBuilderBase<?>> extends IEntry<RecipeType<?>> {
    RecipeSerializer<?> getSerializer();

    Class<?> recipeClass();

    B getBuilder(ResourceLocation loc);

    B recipe(IRecipeDataConsumer consumer, ResourceLocation loc);

    default B recipe(IRecipeDataConsumer consumer, String id) {
        return recipe(consumer, new ResourceLocation(consumer.modid(), id));
    }

    default B recipe(IRecipeDataConsumer consumer, ILoc loc) {
        return recipe(consumer, loc.loc());
    }

    default B recipe(IRecipeDataConsumer consumer, IForgeRegistryEntry<?> entry) {
        var loc = entry.getRegistryName();
        assert loc != null;
        return recipe(consumer, loc);
    }

    interface BuilderFactory<B extends IRecipeBuilderBase<?>> {
        B create(IRecipeType<B> parent, ResourceLocation loc);
    }
}
