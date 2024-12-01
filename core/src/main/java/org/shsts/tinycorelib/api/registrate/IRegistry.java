package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistry<V extends IForgeRegistryEntry<V>> extends IEntry<IForgeRegistry<V>> {
    IEntryHandler<V> getHandler();
}
