package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IEntryHandler<T> {
    <U extends T> IEntry<U> getEntry(ResourceLocation loc);

    <U extends T> IEntry<U> getEntry(String id);
}
