package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IReturnEvent<A, R> extends IEvent<A> {
    R defaultResult();

    Result<R> createResult();

    @FunctionalInterface
    interface Handler<A, R> {
        void handle(A arg, Result<R> result);
    }

    interface Result<R> {
        R get();

        void set(R value);
    }
}
