package org.shsts.tinycorelib.content.registrate.entry;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.recipe.IRecipeDataConsumer;
import org.shsts.tinycorelib.api.recipe.IRecipeSerializer;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipeSerializer;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeEntry<C, R extends IRecipe<C>, B extends IRecipeBuilderBase<R>>
    extends Entry<RecipeType<?>> implements IRecipeType<B> {
    @Nullable
    private RecipeSerializer<?> serializer = null;

    public RecipeTypeEntry(ResourceLocation loc,
        Supplier<SmartRecipeType<C, R, B>> supplier) {
        super(loc, supplier::get);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SmartRecipeType<C, R, B> get() {
        return (SmartRecipeType<C, R, B>) super.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        if (serializer == null) {
            serializer = RegistryObject.create(loc(), ForgeRegistries.RECIPE_SERIALIZERS).get();
        }
        return serializer;
    }

    @Override
    public Class<? extends R> recipeClass() {
        return get().recipeClass;
    }

    public void setSerializer(RecipeSerializer<?> value) {
        serializer = value;
    }

    @Override
    public B getBuilder(ResourceLocation loc) {
        return get().builderFactory.create(this, loc);
    }

    @SuppressWarnings("unchecked")
    private IRecipeSerializer<R, B> getSmartSerializer() {
        return ((SmartRecipeSerializer<C, R, B>) getSerializer()).compose;
    }

    private class Finished implements FinishedRecipe {
        private final ResourceLocation loc;
        private final R compose;

        private Finished(ResourceLocation loc, R compose) {
            this.loc = loc;
            this.compose = compose;
        }

        @Override
        public void serializeRecipeData(JsonObject jo) {
            getSmartSerializer().toJson(jo, compose);
        }

        @Override
        public ResourceLocation getId() {
            return loc;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return getSerializer();
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

    private ResourceLocation normalizeLoc(IRecipeDataConsumer consumer, ResourceLocation loc) {
        var modid = consumer.modid();
        var id = loc.getPath();
        if (!modid.equals(loc.getNamespace())) {
            id = loc.getNamespace() + "/" + id;
        }
        id = get().prefix + "/" + id;
        return new ResourceLocation(modid, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public B recipe(IRecipeDataConsumer consumer, ResourceLocation loc) {
        var loc1 = normalizeLoc(consumer, loc);
        var builder = getBuilder(loc1);
        if (builder instanceof IRecipeBuilder<?, ?> recipeBuilder) {
            recipeBuilder.onBuild(() -> consumer.registerRecipe(loc1, () -> {
                var recipe = recipeBuilder.buildObject();
                return new Finished(loc1, (R) recipe);
            }));
        } else if (builder instanceof IVanillaRecipeBuilder<?, ?> vanillaBuilder) {
            vanillaBuilder.onBuild(() -> consumer.registerRecipe(loc1,
                vanillaBuilder::buildObject));
        }
        return get().defaults.apply(builder);
    }
}
