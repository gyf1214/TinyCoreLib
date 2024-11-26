package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.datagen.api.builder.IDataBuilder;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class EntryDataBuilder<U, P, S extends IDataBuilder<P, S>> extends DataBuilder<P, S> {
    protected final Supplier<U> object;
    protected final List<Runnable> callbacks = new ArrayList<>();

    public EntryDataBuilder(DataGen dataGen, P parent, ResourceLocation loc,
        Supplier<U> object) {
        super(dataGen, parent, loc);
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
    }
}
