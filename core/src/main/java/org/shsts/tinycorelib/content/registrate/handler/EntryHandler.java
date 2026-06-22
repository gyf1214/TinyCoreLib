package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.EntryBuilder;
import org.shsts.tinycorelib.content.registrate.entry.Entry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntryHandler<V> implements IEntryHandler<V> {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected final String modid;
    protected final List<EntryBuilder<V, ?, ?, ?>> builders = new ArrayList<>();
    protected final ResourceKey<? extends Registry<V>> registryKey;
    @Nullable
    private final Supplier<Registry<V>> registrySupp;
    @Nullable
    private Registry<V> registry;

    public EntryHandler(Registrate registrate, ResourceKey<? extends Registry<V>> registryKey,
        Registry<V> registry) {
        this.modid = registrate.modid;
        this.registryKey = registryKey;
        this.registry = registry;
        this.registrySupp = null;
    }

    public EntryHandler(Registrate registrate, ResourceKey<? extends Registry<V>> registryKey,
        Supplier<Registry<V>> registry) {
        this.modid = registrate.modid;
        this.registryKey = registryKey;
        this.registry = null;
        this.registrySupp = registry;
    }

    protected void registerBuilders(Registry<V> registry) {
        LOGGER.info("Mod {} registry {} register {} objects", modid,
            registryKey.location(), builders.size());
        for (var builder : builders) {
            builder.registerObject(registry);
        }
    }

    protected void onRegisterEvent(RegisterEvent event) {
        if (builders.isEmpty()) {
            return;
        }
        var registry = event.getRegistry(registryKey);
        if (registry != null) {
            registerBuilders(registry);
            // free reference
            builders.clear();
        }
    }

    public Registry<V> getRegistry() {
        if (registry != null) {
            return registry;
        }
        assert registrySupp != null;
        registry = registrySupp.get();
        return registry;
    }

    public ResourceKey<? extends Registry<V>> registryKey() {
        return registryKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends V> IEntry<U> getEntry(ResourceLocation loc) {
        return new Entry<>(loc, () -> {
            var value = getRegistry().get(loc);
            if (value == null) {
                throw new NoSuchElementException(registryKey.location() + " " + loc);
            }
            return (U) value;
        });
    }

    @Override
    public <U extends V> IEntry<U> getEntry(String id) {
        return getEntry(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public <U extends V> Entry<U> register(EntryBuilder<V, U, ?, ?> builder) {
        builders.add(builder);
        return new Entry<>(builder.loc());
    }

    public void addListener(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterEvent);
    }
}
