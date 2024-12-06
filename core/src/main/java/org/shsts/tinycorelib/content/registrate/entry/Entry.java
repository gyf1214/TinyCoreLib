package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Entry<U> implements IEntry<U> {
    private final ResourceLocation loc;

    @Nullable
    private Supplier<U> supplier;

    @Nullable
    private U object = null;

    public Entry(ResourceLocation loc) {
        this.loc = loc;
        this.supplier = null;
    }

    public Entry(ResourceLocation loc, Supplier<U> supplier) {
        this.loc = loc;
        this.supplier = supplier;
    }

    public Entry(ResourceLocation loc, U object) {
        this.loc = loc;
        this.object = object;
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    @Override
    public U get() {
        if (object != null) {
            return object;
        }
        assert supplier != null;
        setObject(supplier.get());
        return object;
    }

    public void setObject(U value) {
        object = value;
        supplier = null;
    }

    public void setSupplier(Supplier<U> value) {
        supplier = value;
    }
}
