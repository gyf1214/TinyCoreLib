package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DynamicHandler<V> {
    private final ResourceKey<? extends Registry<V>> registryKey;
    private final Supplier<V> dummyFactory;
    private final Set<ResourceLocation> locations = new HashSet<>();

    public DynamicHandler(ResourceKey<? extends Registry<V>> registryKey, Supplier<V> dummyFactory) {
        this.registryKey = registryKey;
        this.dummyFactory = dummyFactory;
    }

    public void register(ResourceLocation loc) {
        locations.add(loc);
    }

    private V dummy(ResourceLocation loc) {
        return dummyFactory.get();
    }

    private void onRegisterEvent(RegisterEvent event) {
        var registry = event.getRegistry(registryKey);
        if (registry == null) {
            return;
        }
        for (var loc : locations) {
            Registry.register(registry, loc, dummy(loc));
        }
    }

    public void addListener(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterEvent);
    }
}
