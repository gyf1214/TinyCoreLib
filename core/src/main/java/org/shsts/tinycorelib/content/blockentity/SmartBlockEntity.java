package org.shsts.tinycorelib.content.blockentity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.shsts.tinycorelib.api.blockentity.ICapabilityBuilder;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IEventSubscriber;
import org.shsts.tinycorelib.api.blockentity.INBTUpdatable;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.shsts.tinycorelib.content.CoreContents.CLIENT_LOAD;
import static org.shsts.tinycorelib.content.CoreContents.CLIENT_TICK;
import static org.shsts.tinycorelib.content.CoreContents.REMOVED_BY_CHUNK;
import static org.shsts.tinycorelib.content.CoreContents.REMOVED_IN_WORLD;
import static org.shsts.tinycorelib.content.CoreContents.SERVER_LOAD;
import static org.shsts.tinycorelib.content.CoreContents.SERVER_TICK;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartBlockEntity extends BlockEntity implements IEventManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CAPS_TAG = "TinyCoreLibCaps";

    private boolean isChunkUnloaded = false;
    private final Map<ResourceLocation, ICapabilityContainer> containers = new HashMap<>();
    private final Map<ICapability<?>, Object> attachedCapabilities = new HashMap<>();
    private final Multimap<IEvent<?>, Consumer<?>> handlers = HashMultimap.create();
    private final Multimap<IReturnEvent<?, ?>, IReturnEvent.Handler<?, ?>> returnHandlers =
        HashMultimap.create();
    private final Map<ResourceLocation, INBTUpdatable<?>> updatableContainers = new HashMap<>();
    private final Set<ResourceLocation> dirtyContainers = new HashSet<>();

    private SmartBlockEntity(SmartBlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static SmartBlockEntity create(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        var ret = new SmartBlockEntity((SmartBlockEntityType) type, pos, state);
        ret.initContainers();
        return ret;
    }

    private void initContainers() {
        var type = (SmartBlockEntityType) getType();
        var builder = new ICapabilityBuilder() {
            @Override
            public <T> void attach(ICapability<T> capability, T value) {
                var old = attachedCapabilities.putIfAbsent(capability, value);
                if (old != null) {
                    LOGGER.warn("Duplicate capability attachment {} on {}, keeping first value",
                        capability.loc(), SmartBlockEntity.this);
                }
            }
        };
        for (var entry : type.containerFactories().entrySet()) {
            var loc = entry.getKey();
            if (containers.containsKey(loc)) {
                throw new IllegalStateException("Duplicate container id " + loc);
            }
            var container = createContainer(loc, entry.getValue());
            containers.put(loc, container);
            if (container instanceof IEventSubscriber subscriber) {
                subscriber.subscribeEvents(this);
            }
            if (container instanceof INBTUpdatable<?> updatable) {
                updatableContainers.put(loc, updatable);
            }
            container.attachCapability(builder);
        }
    }

    private ICapabilityContainer createContainer(
        ResourceLocation loc, Function<BlockEntity, ICapabilityContainer> factory) {
        try {
            var ret = factory.apply(this);
            if (ret == null) {
                throw new IllegalStateException("Container factory returned null for " + loc);
            }
            return ret;
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to create capability container " + loc, ex);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getCapability(ICapability<T> capability) {
        return (T) attachedCapabilities.get(capability);
    }

    @Override
    public <T extends ICapabilityContainer> T getContainer(ResourceLocation loc, Class<T> clazz) {
        return tryGetContainer(loc, clazz).orElseThrow();
    }

    @Override
    public <T extends ICapabilityContainer> Optional<T> tryGetContainer(
        ResourceLocation loc, Class<T> clazz) {
        var container = containers.get(loc);
        return clazz.isInstance(container) ? Optional.of(clazz.cast(container)) : Optional.empty();
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
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        isChunkUnloaded = true;
    }

    @Override
    public final void setRemoved() {
        assert level != null;
        if (!isChunkUnloaded) {
            invoke(REMOVED_IN_WORLD.get(), level);
        } else {
            invoke(REMOVED_BY_CHUNK.get(), level);
        }
        super.setRemoved();
    }

    private void onTick(Level world) {
        if (world.isClientSide) {
            invoke(CLIENT_TICK.get(), world);
        } else {
            invoke(SERVER_TICK.get(), world);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        assert level != null;
        if (level.isClientSide) {
            invoke(CLIENT_LOAD.get(), level);
        } else {
            invoke(SERVER_LOAD.get(), level);
        }
    }

    public static <T extends BlockEntity> void ticker(Level world, T be) {
        if (be instanceof SmartBlockEntity sbe) {
            sbe.onTick(world);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tag.contains(CAPS_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        var caps = tag.getCompound(CAPS_TAG);
        for (var entry : containers.entrySet()) {
            var key = entry.getKey().toString();
            if (entry.getValue() instanceof INBTSerializable<?> serializable &&
                caps.contains(key)) {
                deserializeNBT(serializable, registries, caps.get(key));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var caps = new CompoundTag();
        for (var entry : containers.entrySet()) {
            if (entry.getValue() instanceof INBTSerializable<?> serializable) {
                caps.put(entry.getKey().toString(), serializable.serializeNBT(registries));
            }
        }
        if (!caps.isEmpty()) {
            tag.put(CAPS_TAG, caps);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void deserializeNBT(INBTSerializable serializable,
        HolderLookup.Provider registries, @Nullable Tag tag) {
        if (tag != null) {
            serializable.deserializeNBT(registries, tag);
        }
    }

    private CompoundTag getUpdateTag(boolean forceUpdate, HolderLookup.Provider registries) {
        var tag = new CompoundTag();
        var caps = getCapsUpdateTag(forceUpdate, registries);
        if (!caps.isEmpty()) {
            tag.put(CAPS_TAG, caps);
        }
        return tag;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return getUpdateTag(true, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        if (!shouldSendUpdate()) {
            return null;
        }
        var ret = ClientboundBlockEntityDataPacket.create(this, (be, registries) ->
            ((SmartBlockEntity) be).getUpdateTag(false, registries));
        resetShouldSendUpdate();
        return ret;
    }

    private CompoundTag getCapsUpdateTag(boolean forceUpdate, HolderLookup.Provider registries) {
        var caps = new CompoundTag();
        for (var entry : updatableContainers.entrySet()) {
            var loc = entry.getKey();
            if (!forceUpdate && !dirtyContainers.contains(loc)) {
                continue;
            }
            caps.put(loc.toString(), entry.getValue().serializeOnUpdate(registries));
        }
        return caps;
    }

    private boolean shouldSendUpdate() {
        for (var entry : updatableContainers.entrySet()) {
            if (entry.getValue().shouldSendUpdate()) {
                dirtyContainers.add(entry.getKey());
            }
        }
        return !dirtyContainers.isEmpty();
    }

    private void resetShouldSendUpdate() {
        dirtyContainers.clear();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (!tag.contains(CAPS_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        var caps = tag.getCompound(CAPS_TAG);
        for (var key : caps.getAllKeys()) {
            var container = updatableContainers.get(ResourceLocation.parse(key));
            if (container != null) {
                container.deserializeTagOnUpdate(lookupProvider, caps.get(key));
            }
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
        HolderLookup.Provider lookupProvider) {
        var tag = pkt.getTag();
        handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public String toString() {
        var dimension = level == null ? null : level.dimension().location();
        return getClass().getSimpleName() + "[pos=" + worldPosition + "@" + dimension + "]";
    }
}
