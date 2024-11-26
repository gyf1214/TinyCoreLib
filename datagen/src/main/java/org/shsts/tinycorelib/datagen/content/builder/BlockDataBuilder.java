package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import org.shsts.tinycorelib.datagen.api.builder.IBlockDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;
import org.shsts.tinycorelib.datagen.content.DataGen;

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
        callbacks.add(() -> dataGen.tag(object, List.of(tag)));
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> itemTag(List<TagKey<Item>> tags) {
        callbacks.add(() -> dataGen.tag(() -> object.get().asItem(), tags));
        return self();
    }

    @Override
    public IBlockDataBuilder<U, P> itemTag(TagKey<Item> tag) {
        callbacks.add(() -> dataGen.tag(() -> object.get().asItem(), List.of(tag)));
        return self();
    }

    @Override
    protected void doRegister() {
        assert blockState != null;
        if (itemModel == null) {
            itemModel = ctx -> ctx.provider().withExistingParent(ctx.id(),
                new ResourceLocation(ctx.modid(), "block/" + ctx.id()));
        }
        dataGen.blockStateHandler.addBlockStateCallback(loc, object, blockState);
        dataGen.itemModelHandler.addBlockItemCallback(loc, object, ctx -> itemModel.accept(ctx));
    }
}
