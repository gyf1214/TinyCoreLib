package org.shsts.tinycorelib.content.registrate.builder;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.registrate.builder.IEntryBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.Entry;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class EntryBuilder<T extends IForgeRegistryEntry<T>, U extends T, P,
    S extends IEntryBuilder<T, U, P, S>> extends Builder<U, P, S> implements IEntryBuilder<T, U, P, S> {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected final Registrate registrate;
    protected final EntryHandler<T> handler;
    protected final ResourceLocation loc;

    @Nullable
    protected Entry<U> entry = null;

    public EntryBuilder(Registrate registrate, EntryHandler<T> handler, P parent, String id) {
        super(parent);
        this.registrate = registrate;
        this.handler = handler;
        this.loc = new ResourceLocation(registrate.modid, id);
        onBuild.add(this::register);
    }

    protected Entry<U> createEntry() {
        return handler.register(this);
    }

    public void registerObject(IForgeRegistry<T> registry) {
        LOGGER.trace("register object {} {}", registry.getRegistryName(), loc);
        assert entry != null;
        var object = createObject();
        object.setRegistryName(loc);
        registry.register(object);
        for (var cb : onCreateObject) {
            cb.accept(object);
        }
        onCreateObject.clear();
        entry.setObject(object);
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    @Override
    public IEntry<U> register() {
        LOGGER.trace("create entry {} {}", getClass().getSimpleName(), loc);
        entry = createEntry();
        return entry;
    }
}
