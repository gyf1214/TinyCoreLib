package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataHandler<P extends DataProvider> {
    void addCallback(Consumer<P> cb);

    P createProvider(GatherDataEvent event);

    /**
     * Called at run() to add callbacks.
     */
    void register(P provider);

    <B> B builder(String id, BiFunction<IDataHandler<P>, ResourceLocation, B> factory);

    @FunctionalInterface
    interface ProviderFactory<P extends DataProvider> {
        P create(IDataGen dataGen, IDataHandler<P> handler, GatherDataEvent event);
    }
}
