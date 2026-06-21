package org.shsts.tinycorelib.api.core;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface DistLazy<T> extends Supplier<Supplier<T>> {
    default T getValue() {
        return get().get();
    }

    default void runOnDist(Dist dist, Supplier<Consumer<T>> cons) {
        if (FMLEnvironment.dist == dist) {
            cons.get().accept(getValue());
        }
    }
}
