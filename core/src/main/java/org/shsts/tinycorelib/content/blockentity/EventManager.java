package org.shsts.tinycorelib.content.blockentity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.shsts.tinycorelib.content.CoreContents.EVENT_MANAGER;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EventManager implements ICapabilityProvider, IEventManager {
    private final LazyOptional<IEventManager> myself;

    private final Map<ResourceLocation, ICapabilityProvider> providers = new HashMap<>();
    private final Multimap<IEvent<?>, Consumer<?>> handlers = HashMultimap.create();
    private final Multimap<IReturnEvent<?, ?>, ReturnEvent.Handler<?, ?>> returnHandlers
        = HashMultimap.create();

    public EventManager() {
        this.myself = LazyOptional.of(() -> this);
    }

    public void addProvider(ResourceLocation loc, ICapabilityProvider provider) {
        providers.put(loc, provider);
    }

    @Override
    public <T extends ICapabilityProvider> T getProvider(ResourceLocation loc, Class<T> clazz) {
        return clazz.cast(providers.get(loc));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> void invoke(IEvent<A> event, A arg) {
        for (var handler : handlers.get(event)) {
            ((Consumer<A>) handler).accept(arg);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A, R> R invokeReturn(IReturnEvent<A, R> event, A arg) {
        var ret = event.createResult();
        for (var handler : returnHandlers.get(event)) {
            ((IReturnEvent.Handler<A, R>) handler).handle(arg, ret);
        }
        return ret.get();
    }

    @Override
    public <A> void subscribe(IEvent<A> event, Consumer<A> handler) {
        handlers.put(event, handler);
    }

    @Override
    public <A, R> void subscribe(IReturnEvent<A, R> event, IReturnEvent.Handler<A, R> handler) {
        returnHandlers.put(event, handler);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
        if (cap == EVENT_MANAGER.get()) {
            return myself.cast();
        }
        return LazyOptional.empty();
    }
}
