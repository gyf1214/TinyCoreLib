package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DynamicHandler<V extends IForgeRegistryEntry<V>> {
    private final Class<V> entryClass;
    private final Supplier<V> dummyFactory;
    private final Set<ResourceLocation> locations = new HashSet<>();

    public DynamicHandler(Class<V> entryClass, Supplier<V> dummyFactory) {
        this.entryClass = entryClass;
        this.dummyFactory = dummyFactory;
    }

    public void register(ResourceLocation loc) {
        locations.add(loc);
    }

    private V dummy(ResourceLocation loc) {
        var object = dummyFactory.get();
        object.setRegistryName(loc);
        return object;
    }

    private void onRegisterEvent(RegistryEvent.Register<V> event) {
        var registry = event.getRegistry();
        for (var loc : locations) {
            registry.register(dummy(loc));
        }
    }

    public void addListener(IEventBus modEventBus) {
        modEventBus.addGenericListener(entryClass, this::onRegisterEvent);
    }
}
