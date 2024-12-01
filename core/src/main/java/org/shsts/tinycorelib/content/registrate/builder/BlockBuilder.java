package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.builder.IBlockBuilder;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.content.registrate.Entry;
import org.shsts.tinycorelib.content.registrate.Registrate;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockBuilder<U extends Block, P> extends EntryBuilder<Block, U, P, IBlockBuilder<U, P>>
    implements IBlockBuilder<U, P> {
    private final Function<BlockBehaviour.Properties, U> factory;
    private Material material = Material.STONE;
    private Transformer<BlockBehaviour.Properties> properties = $ -> $;

    @Nullable
    private IItemBuilder<BlockItem, IBlockBuilder<U, P>> blockItemBuilder = null;
    private boolean noBlockItem = false;
    @Nullable
    private DistLazy<BlockColor> tint = null;

    public BlockBuilder(Registrate registrate, P parent, String id,
        Function<BlockBehaviour.Properties, U> factory) {
        super(registrate, registrate.blockHandler, parent, id);
        this.factory = factory;
        onCreateObject.add(registrate::trackBlock);
    }

    @Override
    public IBlockBuilder<U, P> material(Material value) {
        material = value;
        return self();
    }

    @Override
    public IBlockBuilder<U, P> properties(Transformer<BlockBehaviour.Properties> trans) {
        properties = properties.chain(trans);
        return self();
    }

    @Override
    public IBlockBuilder<U, P> renderType(DistLazy<RenderType> value) {
        onCreateObject.add(block -> value.runOnDist(Dist.CLIENT, () -> type ->
            registrate.renderTypeHandler.setRenderType(block, type)));
        return self();
    }

    @Override
    public IBlockBuilder<U, P> translucent() {
        return renderType(() -> RenderType::cutoutMipped);
    }

    @Override
    public IBlockBuilder<U, P> tint(DistLazy<BlockColor> value) {
        tint = value;
        return self();
    }

    @Override
    public IBlockBuilder<U, P> tint(IntUnaryOperator colors) {
        return tint(() -> () -> ($1, $2, $3, i) -> colors.applyAsInt(i));
    }

    @Override
    public IBlockBuilder<U, P> tint(int... colors) {
        return tint(() -> () -> ($1, $2, $3, index) -> index < colors.length ?
            colors[index] : 0xFFFFFFFF);
    }

    private class BlockItemBuilder extends ItemBuilder<BlockItem, IBlockBuilder<U, P>> {
        public BlockItemBuilder() {
            super(BlockBuilder.this.registrate, BlockBuilder.this, BlockBuilder.this.id(),
                properties -> {
                    assert BlockBuilder.this.entry != null;
                    return new BlockItem(BlockBuilder.this.entry.get(), properties);
                });
        }

        @Override
        protected Entry<BlockItem> createEntry() {
            if (tint == null) {
                tint = getItemTint();
            }
            return super.createEntry();
        }
    }

    @Override
    public IItemBuilder<BlockItem, IBlockBuilder<U, P>> blockItem() {
        if (blockItemBuilder == null) {
            blockItemBuilder = new BlockItemBuilder();
        }
        return blockItemBuilder;
    }

    @Override
    public IBlockBuilder<U, P> noBlockItem() {
        noBlockItem = true;
        return self();
    }

    @Nullable
    private DistLazy<ItemColor> getItemTint() {
        var tint = this.tint;
        return tint == null ? null : () -> {
            var itemColor = tint.getValue();
            return () -> (itemStack, index) -> {
                var item = (BlockItem) itemStack.getItem();
                return itemColor.getColor(item.getBlock().defaultBlockState(), null, null, index);
            };
        };
    }

    @Override
    protected Entry<U> createEntry() {
        var tint = this.tint;
        if (tint != null) {
            onCreateObject.add(block -> tint.runOnDist(Dist.CLIENT, () -> blockColor ->
                registrate.tintHandler.addBlockColor(block, blockColor)));
        }
        if (blockItemBuilder == null && !noBlockItem) {
            blockItem().build();
        }
        if (blockItemBuilder != null) {
            blockItemBuilder.register();
        }
        return super.createEntry();
    }

    @Override
    protected U createObject() {
        return factory.apply(properties.apply(BlockBehaviour.Properties.of(material)));
    }
}
