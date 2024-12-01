package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ISimpleEntryBuilder<T extends IForgeRegistryEntry<T>, U extends T, P>
    extends IEntryBuilder<T, U, P, ISimpleEntryBuilder<T, U, P>> {
}
