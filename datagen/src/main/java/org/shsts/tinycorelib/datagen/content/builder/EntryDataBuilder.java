package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.datagen.api.builder.IDataBuilder;
import org.shsts.tinycorelib.datagen.content.DataGen;
import org.shsts.tinycorelib.datagen.content.context.TrackedContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class EntryDataBuilder<T, U extends T, P, S extends IDataBuilder<P, S>>
    extends DataBuilder<P, S> {
    protected final ResourceKey<T> key;
    protected final TrackedContext<T> trackedCtx;
    protected final U object;
    protected final List<Runnable> callbacks = new ArrayList<>();

    public EntryDataBuilder(DataGen dataGen, P parent,
        ResourceKey<? extends Registry<T>> registryKey, IEntry<U> entry,
        TrackedContext<T> ctx) {
        this(dataGen, parent, ResourceKey.create(registryKey, entry.loc()),
            entry.get(), ctx);
    }

    public EntryDataBuilder(DataGen dataGen, P parent, Registry<T> registry,
        U object, TrackedContext<T> ctx) {
        this(dataGen, parent, registry.getResourceKey(object)
            .orElseThrow(() -> new IllegalArgumentException("Object is not registered: " + object)),
            object, ctx);
    }

    private EntryDataBuilder(DataGen dataGen, P parent, ResourceKey<T> key,
        U object, TrackedContext<T> ctx) {
        super(dataGen, parent, key.location());
        this.key = key;
        this.trackedCtx = ctx;
        this.object = object;
    }

    protected Supplier<U> objectSupplier() {
        return () -> object;
    }

    protected abstract void doRegister();

    @Override
    protected void register() {
        doRegister();
        for (var cb : callbacks) {
            cb.run();
        }
        callbacks.clear();
        trackedCtx.process(object);
    }
}
