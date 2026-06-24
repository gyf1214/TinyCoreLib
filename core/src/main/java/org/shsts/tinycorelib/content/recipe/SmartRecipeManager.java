package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.registrate.entry.Entry;
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
    private <C, R extends IRecipe<C>> RecipeType<SmartRecipe<C, R>> getType(
        IRecipeType<R> type) {
        return ((RecipeTypeEntry<C, R>) type).get();
    }

    private <R extends IRecipe<?>> IEntry<R> unwrap(RecipeHolder<? extends SmartRecipe<?, R>> holder) {
        return new Entry<>(holder.id(), holder.value().compose);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public <C, R extends IRecipe<C>> Optional<IEntry<R>> getRecipeFor(
        IRecipeType<R> type, C container) {
        return manager.getRecipeFor(getType(type), new ContainerWrapper<>(container), null)
            .map(this::unwrap);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public <C, R extends IRecipe<C>> List<IEntry<R>> getRecipesFor(
        IRecipeType<R> type, C container) {
        return manager.getRecipesFor(getType(type), new ContainerWrapper<>(container), null)
            .stream().map(this::unwrap)
            .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends IRecipe<?>> List<IEntry<R>> getAllRecipesFor(IRecipeType<R> type) {
        return (List<IEntry<R>>) (List<?>) getRawRecipesFor(type);
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public List<IEntry<? extends IRecipe<?>>> getRawRecipesFor(IRecipeType<?> type) {
        var ret = new ArrayList<IEntry<? extends IRecipe<?>>>();
        var recipeType = (SmartRecipeType<Object, IRecipe<Object>>) type.get();
        for (var recipe : manager.getAllRecipesFor(recipeType)) {
            ret.add(unwrap(recipe));
        }
        return ret;
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public <R extends IRecipe<?>> Optional<IEntry<R>> byLoc(
        IRecipeType<R> type, ResourceLocation loc) {
        var clazz = type.recipeClass();
        return manager.byKey(loc)
            .flatMap($ -> $.value() instanceof SmartRecipe<?, ?> smartRecipe &&
                smartRecipe.getType() == type.get() ?
                Optional.of(new Entry<>(loc, clazz.cast(smartRecipe.compose))) : Optional.empty());
    }

    @Override
    public Optional<IEntry<? extends IRecipe<?>>> byLoc(ResourceLocation loc) {
        return manager.byKey(loc)
            .flatMap($ -> $.value() instanceof SmartRecipe<?, ?> smartRecipe ?
                Optional.of(new Entry<>(loc, smartRecipe.compose)) : Optional.empty());
    }
}
