package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.entry.IRegistry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRegistryBuilder<V, P>
    extends ILoc, IBuilder<RegistryBuilder<V>, P, IRegistryBuilder<V, P>> {
    IRegistryBuilder<V, P> builder(Transformer<RegistryBuilder<V>> trans);

    IRegistryBuilder<V, P> onBake(BakeCallback<V> cb);

    IRegistry<V> register();
}
