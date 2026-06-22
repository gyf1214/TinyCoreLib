package org.shsts.tinycorelib.content.blockentity;

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
import org.shsts.tinycorelib.api.blockentity.ICapabilityBuilder;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.shsts.tinycorelib.content.CoreContents.CLIENT_LOAD;
import static org.shsts.tinycorelib.content.CoreContents.CLIENT_TICK;
import static org.shsts.tinycorelib.content.CoreContents.EVENT_MANAGER;
import static org.shsts.tinycorelib.content.CoreContents.REMOVED_BY_CHUNK;
import static org.shsts.tinycorelib.content.CoreContents.REMOVED_IN_WORLD;
import static org.shsts.tinycorelib.content.CoreContents.SERVER_LOAD;
import static org.shsts.tinycorelib.content.CoreContents.SERVER_TICK;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean isChunkUnloaded = false;
    private final Map<ResourceLocation, ICapabilityContainer> containers = new HashMap<>();
    private final Map<ICapability<?>, Object> attachedCapabilities = new HashMap<>();

    @Nullable
    private EventManager eventManager = null;

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
            container.attachCapability(builder);
        }
    }

    private ICapabilityContainer createContainer(
        ResourceLocation loc, java.util.function.Function<BlockEntity, ICapabilityContainer> factory) {
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

    private Optional<EventManager> getEventManager() {
        if (eventManager == null) {
            eventManager = (EventManager) EVENT_MANAGER.tryGet(this).orElse(null);
        }
        return Optional.ofNullable(eventManager);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        isChunkUnloaded = true;
    }

    @Override
    public final void setRemoved() {
        assert level != null;
        var eventManager = getEventManager();
        if (!isChunkUnloaded) {
            eventManager.ifPresent($ -> $.invoke(REMOVED_IN_WORLD.get(), level));
        } else {
            eventManager.ifPresent($ -> $.invoke(REMOVED_BY_CHUNK.get(), level));
        }
        super.setRemoved();
    }

    private void onTick(Level world) {
        var eventManager = getEventManager();
        if (world.isClientSide) {
            eventManager.ifPresent($ -> $.invoke(CLIENT_TICK.get(), world));
        } else {
            eventManager.ifPresent($ -> $.invoke(SERVER_TICK.get(), world));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        assert level != null;
        var eventManager = getEventManager();
        if (level.isClientSide) {
            eventManager.ifPresent($ -> $.invoke(CLIENT_LOAD.get(), level));
        } else {
            eventManager.ifPresent($ -> $.invoke(SERVER_LOAD.get(), level));
        }
    }

    public static <T extends BlockEntity> void ticker(Level world, T be) {
        if (be instanceof SmartBlockEntity sbe) {
            sbe.onTick(world);
        }
    }

    private CompoundTag getUpdateTag(boolean forceUpdate) {
        var tag = new CompoundTag();
        getEventManager().ifPresent($ -> {
            var caps = $.getUpdateTag(forceUpdate);
            tag.put("ForgeCaps", caps);
        });
        return tag;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return getUpdateTag(true);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return getEventManager()
            .filter(EventManager::shouldSendUpdate)
            .map($ -> {
                var ret = ClientboundBlockEntityDataPacket.create(this, (be, registries) ->
                    ((SmartBlockEntity) be).getUpdateTag(false));
                $.resetShouldSendUpdate();
                return ret;
            }).orElse(null);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (!tag.contains("ForgeCaps", Tag.TAG_LIST)) {
            return;
        }
        var caps = tag.getList("ForgeCaps", Tag.TAG_COMPOUND);
        getEventManager().ifPresent($ -> $.handleUpdateTag(caps));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
        HolderLookup.Provider lookupProvider) {
        var tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag, lookupProvider);
        }
    }

    @Override
    public String toString() {
        var dimension = level == null ? null : level.dimension().location();
        return getClass().getSimpleName() + "[pos=" + worldPosition + "@" + dimension + "]";
    }
}
