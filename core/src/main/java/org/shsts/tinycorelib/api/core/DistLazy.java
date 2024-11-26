package org.shsts.tinycorelib.api.core;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface DistLazy<T> extends Supplier<Supplier<T>> {
    default T getValue() {
        return get().get();
    }

    default void runOnDist(Dist dist, Supplier<Consumer<T>> cons) {
        DistExecutor.unsafeRunWhenOn(dist, () -> () -> cons.get().accept(getValue()));
    }
}
