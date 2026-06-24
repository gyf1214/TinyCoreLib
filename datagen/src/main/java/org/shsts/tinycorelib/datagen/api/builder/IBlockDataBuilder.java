package org.shsts.tinycorelib.datagen.api.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;

import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockDataBuilder<U extends Block, P>
    extends IDataBuilder<P, IBlockDataBuilder<U, P>> {
    IBlockDataBuilder<U, P> blockState(
        Consumer<IEntryDataContext<U, BlockStateProvider>> cons);

    <U1 extends BlockItem> IBlockDataBuilder<U, P> itemModel(
        Consumer<IEntryDataContext<U1, ItemModelProvider>> cons);

    IBlockDataBuilder<U, P> tag(List<TagKey<Block>> tags);

    IBlockDataBuilder<U, P> tag(TagKey<Block> tag);

    IBlockDataBuilder<U, P> itemTag(List<TagKey<Item>> tags);

    IBlockDataBuilder<U, P> itemTag(TagKey<Item> tag);

    IBlockDataBuilder<U, P> noDrop();

    IBlockDataBuilder<U, P> drop(ItemLike item, float chance);

    IBlockDataBuilder<U, P> drop(IEntry<? extends ItemLike> item, float chance);

    IBlockDataBuilder<U, P> drop(ItemLike item);

    IBlockDataBuilder<U, P> drop(IEntry<? extends ItemLike> item);

    IBlockDataBuilder<U, P> dropSelf();

    IBlockDataBuilder<U, P> dropOnState(ItemLike item,
        BooleanProperty prop, boolean value);

    IBlockDataBuilder<U, P> dropOnState(IEntry<? extends ItemLike> item,
        BooleanProperty prop, boolean value);

    <V extends Comparable<V> & StringRepresentable> IBlockDataBuilder<U, P> dropOnState(
        ItemLike item, Property<V> prop, V value);

    <V extends Comparable<V> & StringRepresentable> IBlockDataBuilder<U, P> dropOnState(
        IEntry<? extends ItemLike> item, Property<V> prop, V value);

    IBlockDataBuilder<U, P> dropSelfOnTool(TagKey<Item> tool);
}
