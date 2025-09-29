package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;

import java.util.function.IntUnaryOperator;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockBuilder<U extends Block, P>
    extends IEntryBuilder<Block, U, P, IBlockBuilder<U, P>> {
    IBlockBuilder<U, P> material(Material value, MaterialColor color);

    default IBlockBuilder<U, P> material(Material value) {
        return material(value, value.getColor());
    }

    IBlockBuilder<U, P> properties(Transformer<Block.Properties> trans);

    IBlockBuilder<U, P> renderType(DistLazy<RenderType> value);

    IBlockBuilder<U, P> translucent();

    IBlockBuilder<U, P> tint(DistLazy<BlockColor> value);

    IBlockBuilder<U, P> tint(IntUnaryOperator colors);

    IBlockBuilder<U, P> tint(int... colors);

    IItemBuilder<BlockItem, IBlockBuilder<U, P>> blockItem();

    IBlockBuilder<U, P> noBlockItem();
}
