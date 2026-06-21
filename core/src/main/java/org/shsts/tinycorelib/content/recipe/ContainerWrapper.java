package org.shsts.tinycorelib.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ContainerWrapper<C>(C compose) implements RecipeInput {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }
}
