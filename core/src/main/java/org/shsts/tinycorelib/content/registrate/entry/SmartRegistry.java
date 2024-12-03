package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.registrate.IEntryHandler;
import org.shsts.tinycorelib.api.registrate.entry.IRegistry;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRegistry<V extends IForgeRegistryEntry<V>> extends Entry<IForgeRegistry<V>>
    implements IRegistry<V> {
    @Nullable
    private EntryHandler<V> handler;

    public SmartRegistry(ResourceLocation loc) {
        super(loc);
    }

    public void setHandler(EntryHandler<V> value) {
        handler = value;
    }

    @Override
    public IEntryHandler<V> getHandler() {
        assert handler != null;
        return handler;
    }
}
