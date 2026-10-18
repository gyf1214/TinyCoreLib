package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestEntityBlock extends Block implements EntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final IBlockEntityType blockEntityType;
    @Nullable
    private final IMenuType menuType;

    public TestEntityBlock(Properties properties, IBlockEntityType blockEntityType,
        @Nullable IMenuType menuType) {
        super(properties);
        this.blockEntityType = blockEntityType;
        this.menuType = menuType;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state,
        BlockEntityType<T> type) {
        return blockEntityType.ticker();
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(world, state, pos, entity, fallDistance);
        if (!world.isClientSide) {
            blockEntityType.get(world, pos)
                .flatMap(TEST_CAPABILITY::tryGet)
                .ifPresent(ITestCapability::foo);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos,
        Player player, BlockHitResult hitResult) {
        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer && menuType != null) {
            menuType.open(serverPlayer, pos);
            return InteractionResult.CONSUME;
        } else {
            var seconds = blockEntityType.get(world, pos)
                .flatMap(TEST_CAPABILITY::tryGet)
                .map(ITestCapability::getSeconds)
                .orElse(0);
            LOGGER.info("seconds = {}", seconds);
            return InteractionResult.SUCCESS;
        }
    }
}
