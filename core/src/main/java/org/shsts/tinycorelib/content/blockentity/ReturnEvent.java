package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReturnEvent<A, R> extends Event<A> implements IReturnEvent<A, R> {
    private final R defaultResult;

    public ReturnEvent(R defaultResult) {
        this.defaultResult = defaultResult;
    }

    @Override
    public R defaultResult() {
        return defaultResult;
    }

    @Override
    public IReturnEvent.Result<R> createResult() {
        return new Result<>(defaultResult);
    }

    public static class Result<R> implements IReturnEvent.Result<R> {
        private R ret;

        private Result(R ret) {
            this.ret = ret;
        }

        @Override
        public R get() {
            return ret;
        }

        @Override
        public void set(R value) {
            ret = value;
        }
    }
}
