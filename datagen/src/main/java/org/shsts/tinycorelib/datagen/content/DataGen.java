package org.shsts.tinycorelib.datagen.content;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedType;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.IDataHandler;
import org.shsts.tinycorelib.datagen.api.builder.IBlockDataBuilder;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;
import org.shsts.tinycorelib.datagen.content.builder.BlockDataBuilder;
import org.shsts.tinycorelib.datagen.content.builder.ItemDataBuilder;
import org.shsts.tinycorelib.datagen.content.context.TrackedContext;
import org.shsts.tinycorelib.datagen.content.handler.BlockStateHandler;
import org.shsts.tinycorelib.datagen.content.handler.DataHandler;
import org.shsts.tinycorelib.datagen.content.handler.ItemModelHandler;
import org.shsts.tinycorelib.datagen.content.handler.LootTableHandler;
import org.shsts.tinycorelib.datagen.content.handler.RecipeHandler;
import org.shsts.tinycorelib.datagen.content.handler.TagsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DataGen implements IDataGen {
    public final String modid;

    public final BlockStateHandler blockStateHandler;
    public final ItemModelHandler itemModelHandler;
    public final LootTableHandler lootTableHandler;
    public final RecipeHandler recipeHandler;

    public final TrackedContext<Block> blockTrackedContext;
    public final TrackedContext<Item> itemTrackedContext;
    public final TrackedContext<String> langTrackedContext;

    private final Registrate registrate;
    private final List<DataHandler<?>> dataHandlers;
    private final List<BiFunction<IDataGen, GatherDataEvent, ? extends DataProvider>> dataProviders;
    private final Map<ResourceKey<? extends Registry<?>>, TagsHandler<?>> tagsHandlers;
    private final List<TrackedContext<?>> trackedContexts;

    @SuppressWarnings("deprecation")
    public DataGen(Registrate registrate) {
        this.registrate = registrate;
        this.modid = registrate.modid;

        this.dataHandlers = new ArrayList<>();
        this.dataProviders = new ArrayList<>();
        this.tagsHandlers = new HashMap<>();
        this.trackedContexts = new ArrayList<>();

        this.blockStateHandler = createDataHandler(BlockStateHandler::new);
        this.itemModelHandler = createDataHandler(ItemModelHandler::new);
        this.lootTableHandler = createDataHandler(LootTableHandler::new);
        this.recipeHandler = createDataHandler(RecipeHandler::new);
        createTagsHandler(Registry.BLOCK);
        createTagsHandler(Registry.ITEM);

        this.blockTrackedContext = createTrackedContext(TrackedType.BLOCK);
        this.itemTrackedContext = createTrackedContext(TrackedType.ITEM);
        this.langTrackedContext = createTrackedContext(TrackedType.LANG);
    }

    private <T extends DataHandler<?>> T createDataHandler(Function<DataGen, T> factory) {
        var ret = factory.apply(this);
        dataHandlers.add(ret);
        return ret;
    }

    private <T> void createTagsHandler(Registry<T> registry) {
        var ret = createDataHandler($ -> new TagsHandler<>($, registry));
        tagsHandlers.put(registry.key(), ret);
    }

    private <V> TrackedContext<V> createTrackedContext(TrackedType<V> type) {
        var ret = new TrackedContext<>(registrate, type);
        trackedContexts.add(ret);
        return ret;
    }

    @SuppressWarnings("unchecked")
    private <T> TagsHandler<T> tagsHandler(ResourceKey<? extends Registry<T>> key) {
        assert tagsHandlers.containsKey(key);
        return (TagsHandler<T>) tagsHandlers.get(key);
    }

    @Override
    public String modid() {
        return modid;
    }

    private class SimpleDataHandler<D extends DataProvider> extends DataHandler<D> {
        private final IDataHandler.ProviderFactory<D> factory;

        private SimpleDataHandler(ProviderFactory<D> factory) {
            super(DataGen.this);
            this.factory = factory;
        }

        @Override
        public D createProvider(GatherDataEvent event) {
            return factory.create(DataGen.this, this, event);
        }
    }

    @Override
    public <D extends DataProvider> IDataHandler<D> createHandler(
        IDataHandler.ProviderFactory<D> factory) {
        return createDataHandler($ -> new SimpleDataHandler<>(factory));
    }

    @Override
    public <D extends DataProvider> IDataGen addProvider(
        BiFunction<IDataGen, GatherDataEvent, D> factory) {
        dataProviders.add(factory);
        return this;
    }

    @Override
    public Set<String> getTrackedLang() {
        return langTrackedContext.getTracked();
    }

    @Override
    public <U extends Block> IBlockDataBuilder<U, IDataGen> block(ResourceLocation loc, Supplier<U> item) {
        return new BlockDataBuilder<>(this, this, loc, item);
    }

    @Override
    public <U extends Block> IBlockDataBuilder<U, IDataGen> block(IEntry<U> block) {
        return new BlockDataBuilder<>(this, this, block.loc(), block);
    }

    @Override
    public <U extends Item> IItemDataBuilder<U, IDataGen> item(ResourceLocation loc, Supplier<U> item) {
        return new ItemDataBuilder<>(this, this, loc, item);
    }

    @Override
    public <U extends Item> IItemDataBuilder<U, IDataGen> item(IEntry<U> item) {
        return new ItemDataBuilder<>(this, this, item.loc(), item);
    }

    @Override
    public <T> IDataGen tag(Supplier<? extends T> object, List<TagKey<T>> tags) {
        assert !tags.isEmpty();
        tagsHandler(tags.get(0).registry()).addTags(object, tags);
        return this;
    }

    @Override
    public <T> IDataGen tag(Supplier<? extends T> object, TagKey<T> tag) {
        tagsHandler(tag.registry()).addTags(object, tag);
        return this;
    }

    @Override
    public <T> IDataGen tag(TagKey<T> object, TagKey<T> tag) {
        tagsHandler(tag.registry()).addTag(object, tag);
        return this;
    }

    @Override
    public IDataGen blockModel(Consumer<IDataContext<BlockModelProvider>> cons) {
        blockStateHandler.addBlockModelCallback(cons);
        return this;
    }

    @Override
    public IDataGen itemModel(Consumer<IDataContext<ItemModelProvider>> cons) {
        itemModelHandler.addModelCallback(cons);
        return this;
    }

    @Override
    public IDataGen replaceVanillaRecipe(Supplier<RecipeBuilder> recipe) {
        recipeHandler.registerRecipe(cons -> recipe.get().save(cons));
        return this;
    }

    @Override
    public IDataGen vanillaRecipe(Supplier<RecipeBuilder> recipe, String suffix) {
        recipeHandler.registerRecipe(cons -> {
            var builder = recipe.get();
            var loc = builder.getResult().getRegistryName();
            assert loc != null;
            var prefix = builder instanceof SimpleCookingRecipeBuilder ? "smelt" : "craft";
            var recipeLoc = new ResourceLocation(modid, prefix + "/" + loc.getPath() + suffix);
            builder.save(cons, recipeLoc);
        });
        return this;
    }

    @Override
    public IDataGen nullRecipe(ResourceLocation loc) {
        recipeHandler.registerRecipe(() -> new NullRecipe(loc));
        return this;
    }

    @Override
    public IDataGen trackLang(String key) {
        langTrackedContext.trackExtra(key, key);
        return this;
    }

    @Override
    public IDataGen processLang(String key) {
        langTrackedContext.process(key);
        return this;
    }

    @Override
    public void registerRecipe(ResourceLocation loc, Supplier<FinishedRecipe> recipe) {
        recipeHandler.registerRecipe(recipe);
    }

    @Override
    public void onGatherData(GatherDataEvent event) {
        for (var handler : dataHandlers) {
            handler.onGatherData(event);
        }
        for (var prov : dataProviders) {
            event.getGenerator().addProvider(prov.apply(this, event));
        }
        event.getGenerator().addProvider(new DataProvider() {
            @Override
            public void run(HashCache cache) {
                for (var trackedCtx : trackedContexts) {
                    trackedCtx.postValidate();
                }
            }

            @Override
            public String getName() {
                return "Validation: " + modid;
            }
        });
    }
}
