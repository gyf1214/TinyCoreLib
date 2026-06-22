package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeEntry<C, R extends IRecipe<C>>
    extends Entry<RecipeType<?>> implements IRecipeType<R> {
    @Nullable
    private RecipeSerializer<?> serializer = null;

    public RecipeTypeEntry(ResourceLocation loc,
        Supplier<SmartRecipeType<C, R>> supplier) {
        super(loc, supplier::get);
    }

    public RecipeTypeEntry(ResourceLocation loc) {
        super(loc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SmartRecipeType<C, R> get() {
        return (SmartRecipeType<C, R>) super.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        if (serializer == null) {
            serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(loc());
            if (serializer == null) {
                throw new NoSuchElementException("Recipe serializer " + loc());
            }
        }
        return serializer;
    }

    @Override
    public Class<R> recipeClass() {
        return get().recipeClass;
    }

    public void setSerializer(RecipeSerializer<?> value) {
        serializer = value;
    }
}
