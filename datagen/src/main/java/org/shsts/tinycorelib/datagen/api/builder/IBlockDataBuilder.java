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
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockDataBuilder<U extends Block, P>
    extends IDataBuilder<P, IBlockDataBuilder<U, P>> {
    IBlockDataBuilder<U, P> blockState(
        Consumer<IEntryDataContext<Block, U, BlockStateProvider>> cons);

    <U1 extends BlockItem> IBlockDataBuilder<U, P> itemModel(
        Consumer<IEntryDataContext<Item, U1, ItemModelProvider>> cons);

    IBlockDataBuilder<U, P> tag(List<TagKey<Block>> tags);

    IBlockDataBuilder<U, P> tag(TagKey<Block> tag);

    IBlockDataBuilder<U, P> itemTag(List<TagKey<Item>> tags);

    IBlockDataBuilder<U, P> itemTag(TagKey<Item> tag);

    IBlockDataBuilder<U, P> drop(Supplier<? extends ItemLike> item, float chance);

    IBlockDataBuilder<U, P> drop(Supplier<? extends ItemLike> item);

    IBlockDataBuilder<U, P> dropSelf();

    IBlockDataBuilder<U, P> dropOnState(Supplier<? extends ItemLike> item,
        BooleanProperty prop, boolean value);

    <V extends Comparable<V> & StringRepresentable> IBlockDataBuilder<U, P> dropOnState(
        Supplier<? extends ItemLike> item, Property<V> prop, V value);

    IBlockDataBuilder<U, P> dropSelfOnTool(TagKey<Item> tool);
}
