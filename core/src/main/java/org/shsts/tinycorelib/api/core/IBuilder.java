package org.shsts.tinycorelib.api.core;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBuilder<U, P, S extends IBuilder<U, P, S>> extends ISelf<S> {
    U buildObject();

    P build();

    S onCreateObject(Consumer<U> cons);

    S onBuild(Runnable callback);

    default S transform(Transformer<S> trans) {
        return trans.apply(self());
    }

    default <C> C child(Function<S, C> func) {
        return func.apply(self());
    }
}
