package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.api.registrate.IEntry;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;

import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataGen {
    String modid();

    <U extends Item> IItemDataBuilder<U, IDataGen> item(ResourceLocation loc, Supplier<U> item);

    <U extends Item> IItemDataBuilder<U, IDataGen> item(IEntry<U> item);

    <T> IDataGen tag(Supplier<? extends T> object, List<TagKey<T>> tags);

    <T> IDataGen tag(TagKey<T> object, TagKey<T> tag);

    void onGatherData(GatherDataEvent event);
}
