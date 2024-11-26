package org.shsts.tinycorelib.api.core;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBuilder<U, P, S extends IBuilder<U, P, S>> extends ISelf<S> {
    U buildObject();

    P build();

    S onCreateObject(Consumer<U> cons);

    default S transform(Transformer<S> trans) {
        return trans.apply(self());
    }
}
