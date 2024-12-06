package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistry<V extends IForgeRegistryEntry<V>> extends IEntry<IForgeRegistry<V>> {
    IEntryHandler<V> getHandler();
}
