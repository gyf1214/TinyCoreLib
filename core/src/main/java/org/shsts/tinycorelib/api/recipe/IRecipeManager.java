package org.shsts.tinycorelib.api.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeManager {
    <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> Optional<R> getRecipeFor(
        IRecipeType<B> type, C container, Level world);

    <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> List<R> getRecipesFor(
        IRecipeType<B> type, C container, Level world);

    <R extends IRecipe<?>, B extends IRecipeBuilderBase<R>> List<R> getAllRecipesFor(
        IRecipeType<B> type);

    List<IRecipe<?>> getRawRecipesFor(IRecipeType<?> type);

    <R extends IRecipe<?>, B extends IRecipeBuilderBase<R>> Optional<R> byLoc(
        IRecipeType<B> type, ResourceLocation loc);

    Optional<IRecipe<?>> byLoc(ResourceLocation loc);
}
