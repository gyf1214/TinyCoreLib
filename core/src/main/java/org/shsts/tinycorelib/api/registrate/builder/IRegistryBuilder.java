package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.entry.IRegistry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistryBuilder<V extends IForgeRegistryEntry<V>, P>
    extends ILoc, IBuilder<RegistryBuilder<V>, P, IRegistryBuilder<V, P>> {
    IRegistryBuilder<V, P> builder(Transformer<RegistryBuilder<V>> trans);

    IRegistryBuilder<V, P> onBake(IForgeRegistry.BakeCallback<V> cb);

    IRegistry<V> register();
}
