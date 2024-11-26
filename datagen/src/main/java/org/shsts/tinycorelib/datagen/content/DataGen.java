package org.shsts.tinycorelib.datagen.content;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.api.registrate.IEntry;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.builder.IItemDataBuilder;
import org.shsts.tinycorelib.datagen.content.builder.ItemDataBuilder;
import org.shsts.tinycorelib.datagen.content.handler.DataHandler;
import org.shsts.tinycorelib.datagen.content.handler.ItemModelHandler;
import org.shsts.tinycorelib.datagen.content.handler.TagsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DataGen implements IDataGen {
    public final String modid;

    public final ItemModelHandler itemModelHandler;

    private final Registrate registrate;
    private final List<DataHandler<?>> dataHandlers;
    private final Map<ResourceKey<? extends Registry<?>>, TagsHandler<?>> tagsHandlers;

    @SuppressWarnings("deprecation")
    public DataGen(Registrate registrate) {
        this.registrate = registrate;
        this.modid = registrate.modid;

        this.dataHandlers = new ArrayList<>();
        this.tagsHandlers = new HashMap<>();

        createTagsHandler(Registry.BLOCK);
        createTagsHandler(Registry.ITEM);
        this.itemModelHandler = handler(ItemModelHandler::new);
    }

    private <T extends DataHandler<?>> T handler(Function<DataGen, T> factory) {
        var handler = factory.apply(this);
        dataHandlers.add(handler);
        return handler;
    }

    private <T> void createTagsHandler(Registry<T> registry) {
        var ret = handler($ -> new TagsHandler<>($, registry));
        tagsHandlers.put(registry.key(), ret);
    }

    @SuppressWarnings("unchecked")
    private <T> TagsHandler<T> tagsHandler(ResourceKey<? extends Registry<T>> key) {
        assert tagsHandlers.containsKey(key);
        return (TagsHandler<T>) tagsHandlers.get(key);
    }

    @Override
    public String modid() {
        return modid;
    }

    @Override
    public <U extends Item> IItemDataBuilder<U, IDataGen> item(ResourceLocation loc, Supplier<U> item) {
        return new ItemDataBuilder<>(this, this, loc, item);
    }

    @Override
    public <U extends Item> IItemDataBuilder<U, IDataGen> item(IEntry<U> item) {
        return new ItemDataBuilder<>(this, this, item.loc(), item);
    }

    @Override
    public <T> IDataGen tag(Supplier<? extends T> object, List<TagKey<T>> tags) {
        assert !tags.isEmpty();
        tagsHandler(tags.get(0).registry()).addTags(object, tags);
        return this;
    }

    @Override
    public <T> IDataGen tag(TagKey<T> object, TagKey<T> tag) {
        tagsHandler(tag.registry()).addTag(object, tag);
        return this;
    }

    @Override
    public void onGatherData(GatherDataEvent event) {
        for (var handler : dataHandlers) {
            handler.onGatherData(event);
        }
    }
}
