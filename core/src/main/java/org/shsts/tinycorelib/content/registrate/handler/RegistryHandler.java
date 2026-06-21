package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.RegistryBuilderWrapper;
import org.shsts.tinycorelib.content.registrate.entry.RegistryEntry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegistryHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String modid;
    private final List<RegistryBuilderWrapper<?, ?>> builders = new ArrayList<>();

    public RegistryHandler(Registrate registrate) {
        this.modid = registrate.modid;
    }

    public <V> RegistryEntry<V> register(
        RegistryBuilderWrapper<V, ?> builder) {
        builders.add(builder);
        return new RegistryEntry<>(builder.loc());
    }

    @SuppressWarnings("unchecked")
    public <V> Registry<V> getRegistry(ResourceKey<? extends Registry<V>> key) {
        return (Registry<V>) BuiltInRegistries.REGISTRY.get(key.location());
    }

    public void onNewRegistry(NewRegistryEvent event) {
        LOGGER.info("Mod {} register {} registries", modid, builders.size());
        for (var builder : builders) {
            builder.registerObject(event);
        }
        // free reference
        builders.clear();
    }
}
