package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryManager;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.SmartRegistry;
import org.shsts.tinycorelib.content.registrate.builder.RegistryBuilderWrapper;
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

    public <V extends IForgeRegistryEntry<V>> SmartRegistry<V> register(
        RegistryBuilderWrapper<V, ?> builder) {
        builders.add(builder);
        return new SmartRegistry<>(builder.loc());
    }

    public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(
        ResourceKey<Registry<V>> key) {
        return RegistryManager.ACTIVE.getRegistry(key);
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
