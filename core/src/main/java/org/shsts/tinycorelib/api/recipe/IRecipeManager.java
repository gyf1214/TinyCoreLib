package org.shsts.tinycorelib.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeManager {
    <C, R extends IRecipe<C>> Optional<IEntry<R>> getRecipeFor(
        IRecipeType<R> type, C container);

    <C, R extends IRecipe<C>> List<IEntry<R>> getRecipesFor(
        IRecipeType<R> type, C container);

    <R extends IRecipe<?>> List<IEntry<R>> getAllRecipesFor(IRecipeType<R> type);

    List<IEntry<? extends IRecipe<?>>> getRawRecipesFor(IRecipeType<?> type);

    <R extends IRecipe<?>> Optional<IEntry<R>> byLoc(
        IRecipeType<R> type, ResourceLocation loc);

    Optional<IEntry<? extends IRecipe<?>>> byLoc(ResourceLocation loc);
}
