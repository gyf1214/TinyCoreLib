package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.shsts.tinycorelib.api.registrate.IEntry;
import org.shsts.tinycorelib.api.registrate.IEntryHandler;
import org.shsts.tinycorelib.content.registrate.Entry;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.EntryBuilder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntryHandler<T extends IForgeRegistryEntry<T>> implements IEntryHandler<T> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String modid;
    private final List<EntryBuilder<T, ?, ?, ?>> builders = new ArrayList<>();
    private final Class<T> entryClazz;
    private final Supplier<IForgeRegistry<T>> registry;

    public EntryHandler(Registrate registrate, IForgeRegistry<T> registry) {
        this.modid = registrate.modid;
        this.registry = () -> registry;
        this.entryClazz = registry.getRegistrySuperType();
    }

    public EntryHandler(Registrate registrate, Class<T> entryClazz, Supplier<IForgeRegistry<T>> registry) {
        this.modid = registrate.modid;
        this.registry = registry;
        this.entryClazz = entryClazz;
    }

    private void onRegisterEvent(RegistryEvent.Register<T> event) {
        var registry = event.getRegistry();
        LOGGER.info("Registry {} register {} objects", registry.getRegistryName(), builders.size());
        for (var builder : builders) {
            builder.registerObject(registry);
        }
        // free reference
        builders.clear();
    }

    @Override
    public <U extends T> IEntry<U> getEntry(ResourceLocation loc) {
        return new Entry<>(loc, () -> RegistryObject.<T, U>create(loc, registry.get()).get());
    }

    @Override
    public <U extends T> IEntry<U> getEntry(String id) {
        return getEntry(new ResourceLocation(modid, id));
    }

    public <U extends T> Entry<U> register(EntryBuilder<T, U, ?, ?> builder) {
        builders.add(builder);
        return new Entry<>(builder.loc());
    }

    public void addListener(IEventBus modEventBus) {
        modEventBus.addGenericListener(entryClazz, this::onRegisterEvent);
    }
}
