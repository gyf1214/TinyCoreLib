package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistry<V> extends IEntry<Registry<V>> {
    ResourceKey<Registry<V>> key();

    IEntryHandler<V> getHandler();
}
