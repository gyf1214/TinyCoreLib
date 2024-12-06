package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import org.shsts.tinycorelib.datagen.api.builder.IBlockDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;
import org.shsts.tinycorelib.datagen.content.DataGen;
import org.shsts.tinycorelib.datagen.content.handler.LootTableHandler;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockDataBuilder<U extends Block, P> extends EntryDataBuilder<Block, U, P, IBlockDataBuilder<U, P>>
    implements IBlockDataBuilder<U, P> {
    @Nullable
    private Consumer<IEntryDataContext<Block, U, BlockStateProvider>> blockState = null;
    @Nullable
    private Consumer<IEntryDataContext<Item, ?, ItemModelProvider>> itemModel = null;
    private boolean hasDrop = false;

    public BlockDataBuilder(DataGen dataGen, P parent, ResourceLocation loc, Supplier<U> object) {
        super(dataGen, parent, loc, dataGen.blockTrackedContext, object);
    }

    @Override
    public IBlockDataBuilder<U, P> blockState(
        Consumer<IEntryDataContext<Block, U, BlockStateProvider>> cons) {
        blockState = cons;
        return self();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U1 extends BlockItem> IBlockDataBuilder<U, P> itemModel(
        Consumer<IEntryDataContext<Item, U1, ItemModelProvider>> cons) {
        itemModel = ctx -> cons.accept((IEntryDataContext<Item, U1, ItemModelProvider>) ctx);
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> tag(List<TagKey<Block>> tags) {
        callbacks.add(() -> dataGen.tag(object, tags));
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> tag(TagKey<Block> tag) {
        callbacks.add(() -> dataGen.tag(object, tag));
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> itemTag(List<TagKey<Item>> tags) {
        callbacks.add(() -> dataGen.tag(() -> object.get().asItem(), tags));
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> itemTag(TagKey<Item> tag) {
        callbacks.add(() -> dataGen.tag(() -> object.get().asItem(), tag));
        return self();
    }

    private LootTableHandler getDrop() {
        hasDrop = true;
        return dataGen.lootTableHandler;
    }

    @Override
    public IBlockDataBuilder<U, P> drop(Supplier<? extends ItemLike> item, float chance) {
        getDrop().drop(loc, item, chance);
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> drop(Supplier<? extends ItemLike> item) {
        return drop(item, 1f);
    }

    @Override
    public IBlockDataBuilder<U, P> dropSelf() {
        return drop(() -> object.get().asItem());
    }

    @Override
    public IBlockDataBuilder<U, P> dropOnState(Supplier<? extends ItemLike> item,
        BooleanProperty prop, boolean value) {
        getDrop().dropOnState(loc, item, object, prop, value);
        return self();
    }

    @Override
    public <V extends Comparable<V> & StringRepresentable> IBlockDataBuilder<U, P> dropOnState(
        Supplier<? extends ItemLike> item, Property<V> prop, V value) {
        getDrop().dropOnState(loc, item, object, prop, value);
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> dropSelfOnTool(TagKey<Item> tool) {
        getDrop().dropOnTool(loc, () -> object.get().asItem(), tool);
        return self();
    }

    @Override
    protected void doRegister() {
        assert blockState != null;
        if (itemModel == null) {
            itemModel = ctx -> ctx.provider().withExistingParent(ctx.id(),
                new ResourceLocation(ctx.modid(), "block/" + ctx.id()));
        }
        if (!hasDrop) {
            dropSelf();
        }
        dataGen.blockStateHandler.addBlockStateCallback(loc, object, blockState);
        dataGen.itemModelHandler.addBlockItemCallback(loc, object, ctx -> itemModel.accept(ctx));
    }
}
