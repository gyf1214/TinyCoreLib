package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shsts.tinycorelib.api.blockentity.IEventManager;

import static org.shsts.tinycorelib.content.CoreContents.CLIENT_TICK;
import static org.shsts.tinycorelib.content.CoreContents.EVENT_MANAGER;
import static org.shsts.tinycorelib.content.CoreContents.REMOVED_BY_CHUNK;
import static org.shsts.tinycorelib.content.CoreContents.REMOVED_IN_WORLD;
import static org.shsts.tinycorelib.content.CoreContents.SERVER_TICK;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartBlockEntity extends BlockEntity {
    private boolean isChunkUnloaded = false;
    @Nullable
    private IEventManager eventManager = null;

    public SmartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private IEventManager getEventManager() {
        if (eventManager == null) {
            eventManager = EVENT_MANAGER.get(this);
        }
        return eventManager;
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
            getEventManager().invoke(REMOVED_IN_WORLD.get(), level);
        } else {
            getEventManager().invoke(REMOVED_BY_CHUNK.get(), level);
        }
        super.setRemoved();
    }

    private void onTick(Level world) {
        if (world.isClientSide) {
            getEventManager().invoke(CLIENT_TICK.get(), world);
        } else {
            getEventManager().invoke(SERVER_TICK.get(), world);
        }
    }

    public static <T extends BlockEntity> void ticker(Level world, BlockPos pos, BlockState state, T be) {
        if (be instanceof SmartBlockEntity sbe) {
            sbe.onTick(world);
        }
    }
}
