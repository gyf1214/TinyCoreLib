package org.shsts.tinycorelib.datagen.test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.datagen.api.recipe.IRecipeFactory;
import org.shsts.tinycorelib.test.TestRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestRecipeBuilder
    extends Builder<TestRecipe, IRecipeFactory<TestRecipe, TestRecipeBuilder>, TestRecipeBuilder> {
    private int beginSeconds = 0;
    private int endSeconds = 0;
    @Nullable
    private ItemLike displayItem = null;

    public TestRecipeBuilder(IRecipeFactory<TestRecipe, TestRecipeBuilder> parent) {
        super(parent);
    }

    public TestRecipeBuilder range(int begin, int end) {
        beginSeconds = begin;
        endSeconds = end;
        return self();
    }

    public TestRecipeBuilder displayItem(ItemLike value) {
        displayItem = value;
        return self();
    }

    public TestRecipeBuilder displayItem(IEntry<? extends ItemLike> value) {
        return displayItem(value.get());
    }

    @Override
    protected TestRecipe createObject() {
        assert displayItem != null;
        return new TestRecipe(beginSeconds, endSeconds, new ItemStack(displayItem));
    }
}
