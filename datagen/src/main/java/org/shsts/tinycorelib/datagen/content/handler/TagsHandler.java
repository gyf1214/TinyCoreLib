package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TagsHandler<T> extends DataHandler<TagsHandler<T>.Provider> {
    private final Registry<T> registry;
    private final ResourceKey<? extends Registry<T>> registryKey;

    public TagsHandler(DataGen dataGen, Registry<T> registry) {
        super(dataGen);
        this.registry = registry;
        this.registryKey = registry.key();
    }

    public class Provider extends TagsProvider<T> {
        public Provider(GatherDataEvent event) {
            super(event.getGenerator().getPackOutput(), TagsHandler.this.registryKey,
                event.getLookupProvider(),
                dataGen.modid, event.getExistingFileHelper());
        }

        public void addTag(TagKey<T> key, ResourceKey<T> object) {
            tag(key).add(object);
        }

        public void addTag(TagKey<T> key, TagKey<T> object) {
            tag(key).addTag(object);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            TagsHandler.this.register(this);
        }

        @Override
        public String getName() {
            return "Tags<%s>: %s".formatted(registry.key().location(), modId);
        }
    }

    private void validateTag(TagKey<T> tag) {
        if (!tag.registry().equals(registryKey)) {
            throw new IllegalArgumentException(
                "Tag %s does not match registry %s".formatted(tag, registryKey.location()));
        }
    }

    public void addTags(ResourceKey<T> object, List<TagKey<T>> tags) {
        if (!object.isFor(registryKey)) {
            throw new IllegalArgumentException(
                "Object %s does not match registry %s".formatted(object, registryKey.location()));
        }
        for (var tag : tags) {
            validateTag(tag);
            callbacks.add(prov -> prov.addTag(tag, object));
        }
    }

    public void addTags(ResourceKey<T> object, TagKey<T> tag) {
        addTags(object, List.of(tag));
    }

    public void addTag(TagKey<T> object, TagKey<T> tag) {
        validateTag(object);
        validateTag(tag);
        callbacks.add(prov -> prov.addTag(tag, object));
    }

    @Override
    public Provider createProvider(GatherDataEvent event) {
        return new Provider(event);
    }
}
