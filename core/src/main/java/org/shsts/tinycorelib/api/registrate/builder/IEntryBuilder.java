package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IEntryBuilder<T extends IForgeRegistryEntry<T>, U extends T, P,
    S extends IEntryBuilder<T, U, P, S>> extends ILoc, IBuilder<U, P, S> {
    IEntry<U> register();
}
