package org.shsts.tinycorelib.content.blockentity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.shsts.tinycorelib.api.blockentity.IBlockEntityContainer;
import org.shsts.tinycorelib.api.blockentity.ICapabilityBuilder;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.blockentity.IEvent;
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
public class BlockEntityContainer implements IBlockEntityContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CAPS_TAG = "TinyCoreLibCaps";

    private final SmartBlockEntityType type;
    private boolean isChunkUnloaded = false;
    @Nullable
    private BlockEntity blockEntity;
    private final Map<ResourceLocation, ICapabilityContainer> containers = new HashMap<>();
    private final Map<ICapability<?>, Object> attachedCapabilities = new HashMap<>();
    private final Multimap<IEvent<?>, Consumer<?>> handlers = HashMultimap.create();
    private final Multimap<IReturnEvent<?, ?>, IReturnEvent.Handler<?, ?>> returnHandlers =
        HashMultimap.create();
    private final Map<ResourceLocation, INBTUpdatable<?>> updatableContainers = new HashMap<>();
    private final Set<ResourceLocation> dirtyContainers = new HashSet<>();

    public BlockEntityContainer(SmartBlockEntityType type) {
        this.type = type;
    }

    public void initContainers(BlockEntity blockEntity) {
        if (this.blockEntity != null) {
            throw new IllegalStateException("Block entity container is already initialized");
        }
        this.blockEntity = blockEntity;
        var builder = new ICapabilityBuilder() {
            @Override
            public <T> void attach(ICapability<T> capability, T value) {
                var old = attachedCapabilities.putIfAbsent(capability, value);
                if (old != null) {
                    LOGGER.warn("Duplicate capability attachment {} on {}, keeping first value",
                        capability.loc(), BlockEntityContainer.this.blockEntity);
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
            var ret = factory.apply(blockEntity());
            if (ret == null) {
                throw new IllegalStateException("Container factory returned null for " + loc);
            }
            return ret;
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to create capability container " + loc, ex);
        }
    }

    private BlockEntity blockEntity() {
        if (blockEntity == null) {
            throw new IllegalStateException("Block entity container is not initialized");
        }
        return blockEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getCapability(ICapability<T> capability) {
        return Optional.ofNullable((T) attachedCapabilities.get(capability));
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
        isChunkUnloaded = true;
    }

    @Override
    public void onRemoved() {
        var level = blockEntity().getLevel();
        assert level != null;
        if (!isChunkUnloaded) {
            invoke(REMOVED_IN_WORLD.get(), level);
        } else {
            invoke(REMOVED_BY_CHUNK.get(), level);
        }
    }

    @Override
    public void tick(Level world) {
        if (world.isClientSide) {
            invoke(CLIENT_TICK.get(), world);
        } else {
            invoke(SERVER_TICK.get(), world);
        }
    }

    @Override
    public void onLoad() {
        var level = blockEntity().getLevel();
        assert level != null;
        if (level.isClientSide) {
            invoke(CLIENT_LOAD.get(), level);
        } else {
            invoke(SERVER_LOAD.get(), level);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
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

    @Override
    public void writeUpdateTag(CompoundTag tag, boolean force, HolderLookup.Provider registries) {
        var caps = new CompoundTag();
        for (var entry : updatableContainers.entrySet()) {
            var loc = entry.getKey();
            if (!force && !dirtyContainers.contains(loc)) {
                continue;
            }
            caps.put(loc.toString(), entry.getValue().serializeOnUpdate(registries));
        }
        if (!caps.isEmpty()) {
            tag.put(CAPS_TAG, caps);
        }
    }

    @Override
    public Optional<Packet<ClientGamePacketListener>> createUpdatePacket(
        Function<HolderLookup.Provider, CompoundTag> parentTagFactory) {
        if (!shouldSendUpdate()) {
            return Optional.empty();
        }
        var packet = ClientboundBlockEntityDataPacket.create(blockEntity(), (be, registries) -> {
            var tag = parentTagFactory.apply(registries);
            writeUpdateTag(tag, false, registries);
            return tag;
        });
        resetShouldSendUpdate();
        return Optional.of(packet);
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
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains(CAPS_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        var caps = tag.getCompound(CAPS_TAG);
        for (var key : caps.getAllKeys()) {
            var container = updatableContainers.get(ResourceLocation.parse(key));
            if (container != null) {
                container.deserializeTagOnUpdate(registries, caps.get(key));
            }
        }
    }
}
