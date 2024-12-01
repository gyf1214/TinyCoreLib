package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryManager;
import org.shsts.tinycorelib.content.registrate.SmartRegistry;
import org.shsts.tinycorelib.content.registrate.builder.RegistryBuilderWrapper;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegistryHandler {
    private final List<RegistryBuilderWrapper<?, ?>> builders = new ArrayList<>();

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
        for (var builder : builders) {
            builder.registerObject(event);
        }
        // free reference
        builders.clear();
    }
}
