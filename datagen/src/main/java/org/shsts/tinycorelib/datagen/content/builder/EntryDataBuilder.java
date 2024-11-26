package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
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
    protected final TrackedContext<T> trackedCtx;
    protected final Supplier<U> object;
    protected final List<Runnable> callbacks = new ArrayList<>();

    public EntryDataBuilder(DataGen dataGen, P parent, ResourceLocation loc,
        TrackedContext<T> ctx, Supplier<U> object) {
        super(dataGen, parent, loc);
        this.trackedCtx = ctx;
        this.object = object;
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
