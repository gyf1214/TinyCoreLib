package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemDataBuilder<U extends Item, P> extends EntryDataBuilder<U, P, IItemDataBuilder<U, P>>
    implements IItemDataBuilder<U, P> {
    @Nullable
    private Consumer<IEntryDataContext<Item, U, ItemModelProvider>> model = null;

    public ItemDataBuilder(DataGen dataGen, P parent,
        ResourceLocation loc, Supplier<U> object) {
        super(dataGen, parent, loc, object);
    }

    @Override
    public IItemDataBuilder<U, P> model(Consumer<IEntryDataContext<Item, U, ItemModelProvider>> cons) {
        this.model = cons;
        return self();
    }

    @Override
    public IItemDataBuilder<U, P> tag(List<TagKey<Item>> tags) {
        callbacks.add(() -> dataGen.tag(object, tags));
        return self();
    }

    @Override
    public IItemDataBuilder<U, P> tag(TagKey<Item> tag) {
        callbacks.add(() -> dataGen.tag(object, List.of(tag)));
        return self();
    }

    @Override
    protected void doRegister() {
        assert model != null;
        dataGen.itemModelHandler.addModelCallback(loc, object, model);
    }
}
