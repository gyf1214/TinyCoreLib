package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeManager implements IRecipeManager {
    private final RecipeManager manager;

    public SmartRecipeManager(RecipeManager manager) {
        this.manager = manager;
    }

    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    private <C, R extends IRecipe<C>,
        B extends IRecipeBuilderBase<R>> RecipeType<SmartRecipe<C, R>> getType(
        IRecipeType<B> type) {
        return ((RecipeTypeEntry<C, R, B>) type).get();
    }

    @Override
    public <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> Optional<R> getRecipeFor(
        IRecipeType<B> type, C container, Level world) {
        return manager.getRecipeFor(getType(type), new ContainerWrapper<>(container), world)
            .map($ -> $.compose);
    }

    @Override
    public <C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>> List<R> getRecipesFor(
        IRecipeType<B> type, C container, Level world) {
        return manager.getRecipesFor(getType(type), new ContainerWrapper<>(container), world)
            .stream().map($ -> $.compose)
            .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends IRecipe<?>, B extends IRecipeBuilderBase<R>> List<R> getAllRecipesFor(
        IRecipeType<B> type) {
        return (List<R>) getRawRecipesFor(type);
    }

    @Override
    public List<IRecipe<?>> getRawRecipesFor(IRecipeType<?> type) {
        var ret = new ArrayList<IRecipe<?>>();
        for (var recipe : manager.getAllRecipesFor((SmartRecipeType<?, ?, ?>) type.get())) {
            ret.add(recipe.compose);
        }
        return ret;
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <R extends IRecipe<?>, B extends IRecipeBuilderBase<R>> Optional<R> byLoc(
        IRecipeType<B> type, ResourceLocation loc) {
        var clazz = ((RecipeTypeEntry<?, R, B>) type).recipeClass();
        return manager.byKey(loc)
            .flatMap($ -> $ instanceof SmartRecipe<?, ?> smartRecipe &&
                smartRecipe.getType() == type.get() ?
                Optional.of(smartRecipe.compose) : Optional.empty())
            .map(clazz::cast);
    }

    @Override
    public Optional<IRecipe<?>> byLoc(ResourceLocation loc) {
        return manager.byKey(loc)
            .flatMap($ -> $ instanceof SmartRecipe<?, ?> smartRecipe ?
                Optional.of(smartRecipe.compose) : Optional.empty());
    }
}
