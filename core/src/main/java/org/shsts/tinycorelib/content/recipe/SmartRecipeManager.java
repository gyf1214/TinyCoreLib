package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;

import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeManager implements IRecipeManager {
    private final RecipeManager manager;

    public SmartRecipeManager(RecipeManager manager) {
        this.manager = manager;
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> Optional<R> getRecipeFor(
        IRecipeType<B> type, C container, Level world) {
        var type1 = ((RecipeTypeEntry<C, R, B>) type).get();
        return manager.getRecipeFor(type1, new ContainerWrapper<>(container), world)
            .map($ -> $.compose);
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> List<R> getRecipesFor(
        IRecipeType<B> type, C container, Level world) {
        var type1 = ((RecipeTypeEntry<C, R, B>) type).get();
        return manager.getRecipesFor(type1, new ContainerWrapper<>(container), world)
            .stream().map($ -> $.compose)
            .toList();
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> List<R> getAllRecipesFor(
        IRecipeType<B> type) {
        var type1 = ((RecipeTypeEntry<C, R, B>) type).get();
        return manager.getAllRecipesFor(type1)
            .stream().map($ -> $.compose)
            .toList();
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> Optional<R> byLoc(
        IRecipeType<B> type, ResourceLocation loc) {
        var clazz = ((RecipeTypeEntry<C, R, B>) type).recipeClass();
        return manager.byKey(loc).flatMap($ -> {
            if (!($ instanceof SmartRecipe<?, ?> smartRecipe)) {
                return Optional.empty();
            }
            return clazz.isInstance(smartRecipe.compose) ?
                Optional.of(clazz.cast(smartRecipe.compose)) :
                Optional.empty();
        });
    }
}
