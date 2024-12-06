package org.shsts.tinycorelib.test;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeSerializer;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

import java.util.function.Supplier;

import static net.minecraft.world.item.crafting.RecipeSerializer.SMELTING_RECIPE;
import static org.shsts.tinycorelib.test.All.ITEM_HANDLER_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestVanillaRecipe implements IRecipe<BlockEntity> {
    private final ResourceLocation loc;
    private final SmeltingRecipe smeltingRecipe;
    private final int beginSeconds;

    public TestVanillaRecipe(ResourceLocation loc, SmeltingRecipe smeltingRecipe,
        int beginSeconds) {
        this.loc = loc;
        this.smeltingRecipe = smeltingRecipe;
        this.beginSeconds = beginSeconds;
    }

    @Override
    public boolean matches(BlockEntity container, Level world) {
        var itemHandler = (IItemHandlerModifiable) ITEM_HANDLER_CAPABILITY.get(container);
        var testCap = TEST_CAPABILITY.get(container);
        return smeltingRecipe.matches(new RecipeWrapper(itemHandler), world) &&
            testCap.getSeconds() >= beginSeconds;
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    private static class FinishedCooking extends SimpleCookingRecipeBuilder.Result {
        @SuppressWarnings("DataFlowIssue")
        public FinishedCooking(ResourceLocation id, Ingredient ingredient,
            Item result, int cookingTime) {
            super(id, "", ingredient, result, 0f, cookingTime, null, null,
                SMELTING_RECIPE);
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }

    private static class Finished implements FinishedRecipe {
        private final ResourceLocation loc;
        private final IRecipeType<?> type;
        private final FinishedCooking result;
        private final int beginSeconds;

        private Finished(Builder builder) {
            this.loc = builder.loc;
            this.type = builder.parent;
            assert builder.ingredient != null;
            assert builder.result != null;
            assert builder.cookingTime > 0;
            assert builder.beginSeconds >= 0;
            this.result = new FinishedCooking(loc, builder.ingredient.get(),
                builder.result.get().asItem(), builder.cookingTime);
            this.beginSeconds = builder.beginSeconds;
        }

        @Override
        public void serializeRecipeData(JsonObject jo) {
            result.serializeRecipeData(jo);
            jo.addProperty("beginSeconds", beginSeconds);
        }

        @Override
        public ResourceLocation getId() {
            return loc;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return type.getSerializer();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }

    public static class Builder extends RecipeBuilderBase<TestVanillaRecipe, FinishedRecipe, Builder>
        implements IVanillaRecipeBuilder<TestVanillaRecipe, Builder> {
        @Nullable
        private Supplier<Ingredient> ingredient = null;
        @Nullable
        private Supplier<? extends ItemLike> result = null;
        private int cookingTime = 0;
        private int beginSeconds = -1;

        public Builder(IRecipeType<Builder> parent, ResourceLocation loc) {
            super(parent, loc);
        }

        public Builder ingredient(Supplier<? extends ItemLike> value) {
            ingredient = () -> Ingredient.of(value.get());
            return self();
        }

        public Builder ingredient(TagKey<Item> tag) {
            ingredient = () -> Ingredient.of(tag);
            return self();
        }

        public Builder result(Supplier<? extends ItemLike> value) {
            result = value;
            return self();
        }

        public Builder cookingTime(int value) {
            cookingTime = value;
            return self();
        }

        public Builder beginSeconds(int seconds) {
            beginSeconds = seconds;
            return self();
        }

        @Override
        protected FinishedRecipe createObject() {
            return new Finished(this);
        }
    }

    private static class Serializer implements IVanillaRecipeSerializer<TestVanillaRecipe> {
        @Override
        public TestVanillaRecipe fromJson(ResourceLocation loc, JsonObject jo,
            ICondition.IContext context) {
            var cooking = SMELTING_RECIPE.fromJson(loc, jo, context);
            var beginSeconds = GsonHelper.getAsInt(jo, "beginSeconds");
            return new TestVanillaRecipe(loc, cooking, beginSeconds);
        }

        @Override
        public TestVanillaRecipe fromNetwork(ResourceLocation loc, FriendlyByteBuf buf) {
            var cooking = SMELTING_RECIPE.fromNetwork(loc, buf);
            assert cooking != null;
            var beginSeconds = buf.readVarInt();
            return new TestVanillaRecipe(loc, cooking, beginSeconds);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TestVanillaRecipe recipe) {
            SMELTING_RECIPE.toNetwork(buf, recipe.smeltingRecipe);
            buf.writeVarInt(recipe.beginSeconds);
        }
    }

    public static final IVanillaRecipeSerializer<TestVanillaRecipe> SERIALIZER =
        new Serializer();
}
