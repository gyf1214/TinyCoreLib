package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.registrate.entry.IRegistry;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegistryEntry<V> extends Entry<Registry<V>> implements IRegistry<V> {
    private final ResourceKey<Registry<V>> key;
    @Nullable
    private EntryHandler<V> handler;

    public RegistryEntry(ResourceLocation loc) {
        super(loc);
        this.key = ResourceKey.createRegistryKey(loc);
    }

    public void setHandler(EntryHandler<V> value) {
        handler = value;
    }

    @Override
    public ResourceKey<Registry<V>> key() {
        return key;
    }

    @Override
    public IEntryHandler<V> getHandler() {
        assert handler != null;
        return handler;
    }
}
