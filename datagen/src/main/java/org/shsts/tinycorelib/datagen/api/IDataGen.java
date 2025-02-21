package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.api.recipe.IRecipeDataConsumer;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.datagen.api.builder.IBlockDataBuilder;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataGen extends IRecipeDataConsumer {
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

    IDataGen replaceVanillaRecipe(Supplier<RecipeBuilder> recipe);

    default IDataGen vanillaRecipe(Supplier<RecipeBuilder> recipe) {
        return vanillaRecipe(recipe, "");
    }

    IDataGen vanillaRecipe(Supplier<RecipeBuilder> recipe, String suffix);

    IDataGen nullRecipe(ResourceLocation loc);

    default IDataGen nullRecipe(String loc) {
        return nullRecipe(new ResourceLocation(loc));
    }

    default IDataGen nullRecipe(Item item) {
        var loc = item.getRegistryName();
        assert loc != null;
        return nullRecipe(loc);
    }

    IDataGen trackLang(String key);

    IDataGen processLang(String key);

    void onGatherData(GatherDataEvent event);
}
