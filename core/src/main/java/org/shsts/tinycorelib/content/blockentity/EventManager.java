package org.shsts.tinycorelib.content.blockentity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IEventSubscriber;
import org.shsts.tinycorelib.api.blockentity.INBTUpdatable;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final Map<ResourceLocation, INBTUpdatable<?>> updatableCapability = new HashMap<>();
    private final Set<ResourceLocation> dirtyCapabilities = new HashSet<>();

    public EventManager() {
        this.myself = LazyOptional.of(() -> this);
    }

    public void addProvider(ResourceLocation loc, ICapabilityProvider provider) {
        providers.put(loc, provider);
        if (provider instanceof IEventSubscriber subscriber) {
            subscriber.subscribeEvents(this);
        }
        if (provider instanceof INBTUpdatable<?> updatable) {
            updatableCapability.put(loc, updatable);
        }
    }

    public Tag getUpdateTag(boolean forceUpdate) {
        var listTag = new ListTag();

        for (var entry : updatableCapability.entrySet()) {
            var loc = entry.getKey();
            if (!forceUpdate && !dirtyCapabilities.contains(loc)) {
                continue;
            }
            var cap = entry.getValue();
            var tag1 = new CompoundTag();
            tag1.putString("id", loc.toString());
            tag1.put("data", cap.serializeOnUpdate());
            listTag.add(tag1);
        }
        return listTag;
    }

    public void handleUpdateTag(ListTag tag) {
        for (var subTag : tag) {
            var tag1 = (CompoundTag) subTag;
            var loc = new ResourceLocation(tag1.getString("id"));
            var cap = updatableCapability.get(loc);
            var tag2 = tag1.get("data");
            assert tag2 != null;
            cap.deserializeTagOnUpdate(tag2);
        }
    }

    public boolean shouldSendUpdate() {
        for (var entry : updatableCapability.entrySet()) {
            if (entry.getValue().shouldSendUpdate()) {
                dirtyCapabilities.add(entry.getKey());
            }
        }
        return !dirtyCapabilities.isEmpty();
    }

    public void resetShouldSendUpdate() {
        dirtyCapabilities.clear();
    }

    @Override
    public <T extends ICapabilityProvider> T getProvider(
        ResourceLocation loc, Class<T> clazz) {
        return clazz.cast(providers.get(loc));
    }

    @Override
    public <T extends ICapabilityProvider> Optional<T> tryGetProvider(
        ResourceLocation loc, Class<T> clazz) {
        if (!providers.containsKey(loc)) {
            return Optional.empty();
        }
        var prov = providers.get(loc);
        return clazz.isInstance(prov) ? Optional.of(clazz.cast(prov)) :
            Optional.empty();
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
