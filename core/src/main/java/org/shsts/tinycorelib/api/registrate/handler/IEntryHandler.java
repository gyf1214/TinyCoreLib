package org.shsts.tinycorelib.api.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IEntryHandler<V> {
    <U extends V> IEntry<U> getEntry(ResourceLocation loc);

    <U extends V> IEntry<U> getEntry(String id);
}
