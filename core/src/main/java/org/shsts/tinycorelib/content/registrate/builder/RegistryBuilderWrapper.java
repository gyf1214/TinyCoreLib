package org.shsts.tinycorelib.content.registrate.builder;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.builder.IRegistryBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRegistry;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.RegistryEntry;
import org.shsts.tinycorelib.content.registrate.handler.EntryHandler;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegistryBuilderWrapper<V, P>
    extends Builder<RegistryBuilder<V>, P, IRegistryBuilder<V, P>> implements IRegistryBuilder<V, P> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Registrate registrate;
    private final ResourceLocation loc;
    private final ResourceKey<Registry<V>> registryKey;
    private final Class<V> entryClass;
    @Nullable
    private Transformer<RegistryBuilder<V>> transformer = $ -> $;
    @Nullable
    private RegistryEntry<V> entry = null;

    public RegistryBuilderWrapper(Registrate registrate, P parent, String id, Class<V> entryClass) {
        super(parent);
        this.registrate = registrate;
        this.loc = ResourceLocation.fromNamespaceAndPath(registrate.modid, id);
        this.registryKey = ResourceKey.createRegistryKey(loc);
        this.entryClass = entryClass;
        onBuild.add(this::register);
    }

    @Override
    public IRegistryBuilder<V, P> builder(Transformer<RegistryBuilder<V>> trans) {
        assert transformer != null;
        transformer = transformer.chain(trans);
        return self();
    }

    @Override
    public IRegistryBuilder<V, P> onBake(BakeCallback<V> cb) {
        return builder($ -> $.onBake(cb));
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    @Override
    protected RegistryBuilder<V> createObject() {
        assert transformer != null;
        var builder = transformer.apply(new RegistryBuilder<>(registryKey));
        transformer = null;
        return builder;
    }

    public void registerObject(NewRegistryEvent event) {
        LOGGER.debug("register registry {} {}", entryClass.getSimpleName(), loc);
        assert entry != null;
        var builder = buildObject();
        entry.setObject(event.create(builder));
    }

    @Override
    public IRegistry<V> register() {
        LOGGER.trace("create entry {} {}", getClass().getSimpleName(), loc);
        entry = registrate.registryHandler.register(this);
        var handler = new EntryHandler<>(registrate, registryKey, entry);
        entry.setHandler(handler);
        registrate.addEntryHandler(loc, handler);
        return entry;
    }
}
