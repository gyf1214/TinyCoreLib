package org.shsts.tinycorelib.datagen.test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.datagen.api.recipe.IRecipeFactory;
import org.shsts.tinycorelib.test.TestCookingRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestCookingRecipeBuilder
    extends Builder<TestCookingRecipe, IRecipeFactory<TestCookingRecipe, TestCookingRecipeBuilder>,
    TestCookingRecipeBuilder> {
    @Nullable
    private Ingredient ingredient = null;
    @Nullable
    private ItemLike result = null;
    private int cookingTime = 200;
    private int beginSeconds = 0;

    public TestCookingRecipeBuilder(IRecipeFactory<TestCookingRecipe, TestCookingRecipeBuilder> parent) {
        super(parent);
    }

    public TestCookingRecipeBuilder ingredient(TagKey<Item> value) {
        ingredient = Ingredient.of(value);
        return self();
    }

    public TestCookingRecipeBuilder ingredient(ItemLike value) {
        ingredient = Ingredient.of(value);
        return self();
    }

    public TestCookingRecipeBuilder ingredient(IEntry<? extends ItemLike> value) {
        return ingredient(value.get());
    }

    public TestCookingRecipeBuilder result(ItemLike value) {
        result = value;
        return self();
    }

    public TestCookingRecipeBuilder result(IEntry<? extends ItemLike> value) {
        return result(value.get());
    }

    public TestCookingRecipeBuilder cookingTime(int value) {
        cookingTime = value;
        return self();
    }

    public TestCookingRecipeBuilder beginSeconds(int value) {
        beginSeconds = value;
        return self();
    }

    @Override
    protected TestCookingRecipe createObject() {
        assert ingredient != null;
        assert result != null;
        var recipe = new SmeltingRecipe("", CookingBookCategory.MISC, ingredient,
            new ItemStack(result), 0f, cookingTime);
        return new TestCookingRecipe(recipe, beginSeconds);
    }
}
