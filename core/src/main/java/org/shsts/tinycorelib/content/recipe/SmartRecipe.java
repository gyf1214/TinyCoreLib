package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.recipe.IRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipe<C, R extends IRecipe<C>> implements Recipe<ContainerWrapper<C>> {
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    private final ResourceLocation loc;
    public final R compose;

    public SmartRecipe(RecipeType<?> type,
        RecipeSerializer<SmartRecipe<C, R>> serializer,
        ResourceLocation loc, R compose) {
        this.type = type;
        this.serializer = serializer;
        this.loc = loc;
        this.compose = compose;
    }

    @Override
    public boolean matches(ContainerWrapper<C> wrapper, Level level) {
        return compose.matches(wrapper.compose(), level);
    }

    @Override
    public ItemStack assemble(ContainerWrapper<C> pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return loc;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }
}
