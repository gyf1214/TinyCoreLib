package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.EntryBuilder;
import org.shsts.tinycorelib.content.registrate.entry.Entry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntryHandler<V extends IForgeRegistryEntry<V>> implements IEntryHandler<V> {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected final String modid;
    protected final List<EntryBuilder<V, ?, ?, ?>> builders = new ArrayList<>();
    private final Class<V> entryClass;
    @Nullable
    private final Supplier<IForgeRegistry<V>> registrySupp;
    @Nullable
    private IForgeRegistry<V> registry;

    public EntryHandler(Registrate registrate, IForgeRegistry<V> registry) {
        this.modid = registrate.modid;
        this.registry = registry;
        this.registrySupp = null;
        this.entryClass = registry.getRegistrySuperType();
    }

    public EntryHandler(Registrate registrate, Class<V> entryClass, Supplier<IForgeRegistry<V>> registry) {
        this.modid = registrate.modid;
        this.registry = null;
        this.registrySupp = registry;
        this.entryClass = entryClass;
    }

    private void onRegisterEvent(RegistryEvent.Register<V> event) {
        if (builders.isEmpty()) {
            return;
        }
        var registry = event.getRegistry();
        LOGGER.info("Mod {} registry {} register {} objects", modid,
            registry.getRegistryName(), builders.size());
        for (var builder : builders) {
            builder.registerObject(registry);
        }
        // free reference
        builders.clear();
    }

    public IForgeRegistry<V> getRegistry() {
        if (registry != null) {
            return registry;
        }
        assert registrySupp != null;
        registry = registrySupp.get();
        return registry;
    }

    @Override
    public <U extends V> IEntry<U> getEntry(ResourceLocation loc) {
        return new Entry<>(loc, () -> RegistryObject.<V, U>create(loc, getRegistry()).get());
    }

    @Override
    public <U extends V> IEntry<U> getEntry(String id) {
        return getEntry(new ResourceLocation(modid, id));
    }

    public <U extends V> Entry<U> register(EntryBuilder<V, U, ?, ?> builder) {
        builders.add(builder);
        return new Entry<>(builder.loc());
    }

    public void addListener(IEventBus modEventBus) {
        modEventBus.addGenericListener(entryClass, this::onRegisterEvent);
    }
}
