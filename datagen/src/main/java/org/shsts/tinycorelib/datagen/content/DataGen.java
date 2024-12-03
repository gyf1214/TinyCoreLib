package org.shsts.tinycorelib.datagen.content;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
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
import org.shsts.tinycorelib.datagen.content.handler.TagsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public final TrackedContext<Block> blockTrackedContext;
    public final TrackedContext<Item> itemTrackedContext;
    public final TrackedContext<String> langTrackedContext;

    private final Registrate registrate;
    private final List<DataHandler<?>> dataHandlers;
    private final Map<ResourceKey<? extends Registry<?>>, TagsHandler<?>> tagsHandlers;
    private final List<TrackedContext<?>> trackedContexts;

    @SuppressWarnings("deprecation")
    public DataGen(Registrate registrate) {
        this.registrate = registrate;
        this.modid = registrate.modid;

        this.dataHandlers = new ArrayList<>();
        this.tagsHandlers = new HashMap<>();
        this.trackedContexts = new ArrayList<>();

        this.blockStateHandler = createHandler(BlockStateHandler::new);
        this.itemModelHandler = createHandler(ItemModelHandler::new);
        this.lootTableHandler = createHandler(LootTableHandler::new);
        createTagsHandler(Registry.BLOCK);
        createTagsHandler(Registry.ITEM);

        this.blockTrackedContext = createTrackedContext(TrackedType.BLOCK);
        this.itemTrackedContext = createTrackedContext(TrackedType.ITEM);
        this.langTrackedContext = createTrackedContext(TrackedType.LANG);
    }

    private <T extends DataHandler<?>> T createHandler(Function<DataGen, T> factory) {
        var ret = factory.apply(this);
        dataHandlers.add(ret);
        return ret;
    }

    private <T> void createTagsHandler(Registry<T> registry) {
        var ret = createHandler($ -> new TagsHandler<>($, registry));
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
    public void onGatherData(GatherDataEvent event) {
        for (var handler : dataHandlers) {
            handler.onGatherData(event);
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
