package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataHandler<D extends DataProvider> {
    IDataGen dataGen();

    void addCallback(Consumer<D> cb);

    D createProvider(GatherDataEvent event);

    /**
     * Called at run() to add callbacks.
     */
    void register(D provider);

    <B, P> B builder(P parent, String id, BuilderFactory<B, D, P> factory);

    <B> B builder(String id, BuilderFactory<B, D, IDataHandler<D>> factory);

    @FunctionalInterface
    interface ProviderFactory<D extends DataProvider> {
        D create(IDataGen dataGen, IDataHandler<D> handler, GatherDataEvent event);
    }

    @FunctionalInterface
    interface BuilderFactory<B, D extends DataProvider, P> {
        B create(IDataHandler<D> handler, P parent, ResourceLocation loc);
    }
}
