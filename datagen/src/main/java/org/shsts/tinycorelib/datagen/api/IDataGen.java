package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.datagen.api.builder.IBlockDataBuilder;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataGen {
    String modid();

    <U extends Block> IBlockDataBuilder<U, IDataGen> block(ResourceLocation loc, Supplier<U> item);

    <U extends Block> IBlockDataBuilder<U, IDataGen> block(IEntry<U> block);

    <U extends Item> IItemDataBuilder<U, IDataGen> item(ResourceLocation loc, Supplier<U> item);

    <U extends Item> IItemDataBuilder<U, IDataGen> item(IEntry<U> item);

    <T> IDataGen tag(Supplier<? extends T> object, List<TagKey<T>> tags);

    <T> IDataGen tag(TagKey<T> object, TagKey<T> tag);

    IDataGen blockModel(Consumer<IDataContext<BlockModelProvider>> cons);

    IDataGen itemModel(Consumer<IDataContext<ItemModelProvider>> cons);

    void onGatherData(GatherDataEvent event);
}
