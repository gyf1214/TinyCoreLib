package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipe<C, R extends IRecipe<C>> implements Recipe<ContainerWrapper<C>> {
    private final IRecipeType<R> type;
    public final R compose;

    public SmartRecipe(IRecipeType<R> type, R compose) {
        this.type = type;
        this.compose = compose;
    }

    @Override
    public boolean matches(ContainerWrapper<C> wrapper, Level level) {
        return compose.matches(wrapper.compose());
    }

    @Override
    public ItemStack assemble(ContainerWrapper<C> input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return type.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return type.get();
    }
}
