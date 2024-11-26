package org.shsts.tinycorelib.datagen.api.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;

import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemDataBuilder<U extends Item, P> extends IDataBuilder<P, IItemDataBuilder<U, P>> {
    IItemDataBuilder<U, P> model(Consumer<IEntryDataContext<Item, U, ItemModelProvider>> cons);

    IItemDataBuilder<U, P> tag(List<TagKey<Item>> tags);

    IItemDataBuilder<U, P> tag(TagKey<Item> tag);
}
