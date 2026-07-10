package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockBuilder<U extends Block, P>
    extends IEntryBuilder<Block, U, P, IBlockBuilder<U, P>> {
    IBlockBuilder<U, P> properties(Transformer<Block.Properties> trans);

    IBlockBuilder<U, P> tint(DistLazy<BlockColor> value);

    IBlockBuilder<U, P> tint(IntUnaryOperator colors);

    IBlockBuilder<U, P> tint(int... colors);

    IItemBuilder<BlockItem, IBlockBuilder<U, P>> blockItem(
        BiFunction<Block, Item.Properties, BlockItem> factory);

    IItemBuilder<BlockItem, IBlockBuilder<U, P>> blockItem();

    IBlockBuilder<U, P> creativeTab(ResourceKey<CreativeModeTab> tab);

    IBlockBuilder<U, P> creativeTab(ResourceKey<CreativeModeTab> tab, Function<BlockItem, ItemStack> stack);

    IBlockBuilder<U, P> noBlockItem();
}
