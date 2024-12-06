package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TagsHandler<T> extends DataHandler<TagsProvider<T>> {

    private final Registry<T> registry;

    public TagsHandler(DataGen dataGen, Registry<T> registry) {
        super(dataGen);
        this.registry = registry;
    }

    private class Provider extends TagsProvider<T> {
        public Provider(GatherDataEvent event) {
            super(event.getGenerator(), TagsHandler.this.registry,
                dataGen.modid, event.getExistingFileHelper());
        }

        public void addTag(TagKey<T> key, T object) {
            tag(key).add(object);
        }

        public void addTag(TagKey<T> key, TagKey<T> object) {
            tag(key).addTag(object);
        }

        @Override
        protected void addTags() {
            TagsHandler.this.register(this);
        }

        @Override
        public String getName() {
            return "Tags<%s>: %s".formatted(registry.key().location(), modId);
        }
    }

    public void addTags(Supplier<? extends T> object, List<TagKey<T>> tags) {
        for (var tag : tags) {
            callbacks.add(prov -> ((Provider) prov).addTag(tag, object.get()));
        }
    }

    public void addTags(Supplier<? extends T> object, TagKey<T> tag) {
        callbacks.add(prov -> ((Provider) prov).addTag(tag, object.get()));
    }

    public void addTag(TagKey<T> object, TagKey<T> tag) {
        callbacks.add(prov -> ((Provider) prov).addTag(tag, object));
    }

    @Override
    protected TagsProvider<T> createProvider(GatherDataEvent event) {
        return new Provider(event);
    }
}
