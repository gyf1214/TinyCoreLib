package org.shsts.tinycorelib.test;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipeSerializer;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestRecipe implements IRecipe<ITestCapability> {
    private final ResourceLocation loc;
    private final int beginSeconds, endSeconds;
    public final ItemStack displayItem;

    public TestRecipe(Builder builder) {
        assert builder.beginSeconds >= 0 && builder.endSeconds >= 0;
        assert builder.endSeconds >= builder.beginSeconds;
        assert builder.displayItem != null;
        this.loc = builder.loc;
        this.beginSeconds = builder.beginSeconds;
        this.endSeconds = builder.endSeconds;
        var item = ForgeRegistries.ITEMS.getValue(builder.displayItem);
        this.displayItem = new ItemStack(item);
    }

    @Override
    public boolean matches(ITestCapability container, Level world) {
        var seconds = container.getSeconds();
        return seconds >= beginSeconds && seconds < endSeconds;
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    public static class Builder extends RecipeBuilderBase<TestRecipe, TestRecipe, Builder>
        implements IRecipeBuilder<TestRecipe, Builder> {
        private int beginSeconds = 0;
        private int endSeconds = 0;
        @Nullable
        private ResourceLocation displayItem = null;

        public Builder(IRecipeType<Builder> parent, ResourceLocation loc) {
            super(parent, loc);
        }

        public Builder range(int begin, int end) {
            beginSeconds = begin;
            endSeconds = end;
            return self();
        }

        public Builder displayItem(Item item) {
            displayItem = item.getRegistryName();
            return self();
        }

        public Builder displayItem(ResourceLocation loc) {
            displayItem = loc;
            return self();
        }

        @Override
        protected TestRecipe createObject() {
            return new TestRecipe(this);
        }
    }

    private static class Serializer implements IRecipeSerializer<TestRecipe, TestRecipe.Builder> {
        @Override
        public TestRecipe fromJson(IRecipeType<Builder> type, ResourceLocation loc,
            JsonObject jo, ICondition.IContext context) {
            return type.getBuilder(loc)
                .range(GsonHelper.getAsInt(jo, "beginSeconds"),
                    GsonHelper.getAsInt(jo, "endSeconds"))
                .displayItem(new ResourceLocation(GsonHelper.getAsString(
                    jo, "displayItem")))
                .buildObject();
        }

        @Override
        public void toJson(JsonObject jo, TestRecipe recipe) {
            jo.addProperty("beginSeconds", recipe.beginSeconds);
            jo.addProperty("endSeconds", recipe.endSeconds);
            var loc = recipe.displayItem.getItem().getRegistryName();
            assert loc != null;
            jo.addProperty("displayItem", loc.toString());
        }
    }

    public static final IRecipeSerializer<TestRecipe, TestRecipe.Builder> SERIALIZER =
        new Serializer();
}
