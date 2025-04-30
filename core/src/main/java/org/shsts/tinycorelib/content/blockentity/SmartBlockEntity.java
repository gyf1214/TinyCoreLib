package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
    private boolean isChunkUnloaded = false;
    @Nullable
    private EventManager eventManager = null;

    public SmartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    public CompoundTag getUpdateTag() {
        return getUpdateTag(true);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return getEventManager()
            .filter(EventManager::shouldSendUpdate)
            .map($ -> {
                var ret = ClientboundBlockEntityDataPacket.create(this, be ->
                    ((SmartBlockEntity) be).getUpdateTag(false));
                $.resetShouldSendUpdate();
                return ret;
            }).orElse(null);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (!tag.contains("ForgeCaps", Tag.TAG_LIST)) {
            return;
        }
        var caps = tag.getList("ForgeCaps", Tag.TAG_COMPOUND);
        getEventManager().ifPresent($ -> $.handleUpdateTag(caps));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        var tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    @Override
    public String toString() {
        var dimension = level == null ? null : level.dimension().location();
        return getClass().getSimpleName() + "[pos=" + worldPosition + "@" + dimension + "]";
    }
}
