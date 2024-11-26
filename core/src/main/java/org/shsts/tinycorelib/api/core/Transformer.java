package org.shsts.tinycorelib.api.core;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.function.Function;

@FunctionalInterface
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface Transformer<S> extends Function<S, S> {
    default Transformer<S> chain(Transformer<S> other) {
        return $ -> other.apply(apply($));
    }
}
