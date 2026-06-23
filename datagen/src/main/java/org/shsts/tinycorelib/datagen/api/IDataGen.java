package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.datagen.api.builder.IBlockDataBuilder;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;
import org.shsts.tinycorelib.datagen.api.recipe.IRecipeFactory;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataGen {
    String modid();

    <D extends DataProvider> IDataHandler<D> createHandler(IDataHandler.ProviderFactory<D> factory);

    Set<String> getTrackedLang();

    /**
     * Add a DataProvider without any callbacks.
     */
    <D extends DataProvider> IDataGen addProvider(BiFunction<IDataGen, GatherDataEvent, D> factory);

    <U extends Block> IBlockDataBuilder<U, IDataGen> block(ResourceLocation loc, Supplier<U> item);

    <U extends Block> IBlockDataBuilder<U, IDataGen> block(IEntry<U> block);

    <U extends Item> IItemDataBuilder<U, IDataGen> item(ResourceLocation loc, Supplier<U> item);

    <U extends Item> IItemDataBuilder<U, IDataGen> item(IEntry<U> item);

    <T> IDataGen tag(Supplier<? extends T> object, List<TagKey<T>> tags);

    <T> IDataGen tag(Supplier<? extends T> object, TagKey<T> tag);

    <T> IDataGen tag(TagKey<T> object, TagKey<T> tag);

    IDataGen blockModel(Consumer<IDataContext<BlockModelProvider>> cons);

    IDataGen itemModel(Consumer<IDataContext<ItemModelProvider>> cons);

    <R extends IRecipe<?>, B extends IBuilder<R, IRecipeFactory<R, B>, B>>
    IRecipeFactory<R, B> recipeFactory(
        IRecipeType<R> type, Function<IRecipeFactory<R, B>, B> factory);

    IDataGen replaceVanillaRecipe(Supplier<RecipeBuilder> recipe);

    default IDataGen vanillaRecipe(Supplier<RecipeBuilder> recipe) {
        return vanillaRecipe(recipe, "");
    }

    IDataGen vanillaRecipe(Supplier<RecipeBuilder> recipe, String suffix);

    IDataGen nullRecipe(ResourceLocation loc);

    default IDataGen nullRecipe(String loc) {
        return nullRecipe(ResourceLocation.parse(loc));
    }

    default IDataGen nullRecipe(Item item) {
        return nullRecipe(BuiltInRegistries.ITEM.getKey(item));
    }

    IDataGen trackLocale(String locale);

    IDataGen trackLang(String key);

    IDataGen processLang(String locale, String key);

    void onGatherData(GatherDataEvent event);
}
