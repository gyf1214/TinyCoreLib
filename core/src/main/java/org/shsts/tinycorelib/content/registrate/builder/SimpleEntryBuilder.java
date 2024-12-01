package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.registrate.builder.ISimpleEntryBuilder;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleEntryBuilder<T extends IForgeRegistryEntry<T>, U extends T, P>
    extends EntryBuilder<T, U, P, ISimpleEntryBuilder<T, U, P>> implements ISimpleEntryBuilder<T, U, P> {
    private final Supplier<U> factory;

    public SimpleEntryBuilder(Registrate registrate, EntryHandler<T> handler, P parent,
        String id, Supplier<U> factory) {
        super(registrate, handler, parent, id);
        this.factory = factory;
    }

    @Override
    protected U createObject() {
        return factory.get();
    }
}
