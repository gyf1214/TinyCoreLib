package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IEventSubscriber;
import org.shsts.tinycorelib.api.blockentity.INBTUpdatable;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.ITEM_HANDLER_CAPABILITY;
import static org.shsts.tinycorelib.test.All.SERVER_TICK;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_RECIPE;
import static org.shsts.tinycorelib.test.All.TEST_VANILLA_RECIPE;
import static org.shsts.tinycorelib.test.All.TICK_SECOND;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.CORE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestCapability implements ICapabilityProvider, IEventSubscriber, ITestCapability,
    INBTSerializable<IntTag>, INBTUpdatable<IntTag> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final BlockEntity blockEntity;
    private final LazyOptional<ITestCapability> testCap;
    private final LazyOptional<IItemHandler> itemCap;

    private IEventManager eventManager;
    private boolean isUpdateForced = true;
    private int ticks = 0;
    private int seconds = 0;

    public TestCapability(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        var itemHandler = new ItemStackHandler(1);

        this.testCap = LazyOptional.of(() -> this);
        this.itemCap = LazyOptional.of(() -> itemHandler);
    }

    private void onTick(Level world) {
        ticks++;
        if (ticks >= 20) {
            eventManager.invoke(TICK_SECOND.get());
            ticks = 0;
        }
    }

    private void onTickSecond() {
        LOGGER.info("{}: tick second", this);
        seconds++;
        blockEntity.setChanged();
    }

    @Override
    public void foo() {
        LOGGER.info("{}: moo!", this);
        isUpdateForced = true;
        var world = blockEntity.getLevel();
        assert world != null;
        blockEntity.setChanged();
        var pos = blockEntity.getBlockPos();
        var state = blockEntity.getBlockState();
        world.sendBlockUpdated(pos, state, state, TestBlock.UPDATE_CLIENTS);

        var recipeManager = CORE.recipeManager(world);
        var recipes = recipeManager.getRecipesFor(TEST_RECIPE, this, world);
        for (var recipe : recipes) {
            LOGGER.info("matched test recipe = {}", recipe.loc());
        }
        recipeManager.getRecipeFor(TEST_VANILLA_RECIPE, blockEntity, world)
            .ifPresent(recipe -> {
                var itemHandler = (IItemHandlerModifiable) ITEM_HANDLER_CAPABILITY.get(blockEntity);
                itemHandler.setStackInSlot(0, recipe.getResult());
            });
    }

    @Override
    public int getSeconds() {
        return seconds;
    }

    @Override
    public IntTag serializeNBT() {
        return IntTag.valueOf(seconds);
    }

    @Override
    public void deserializeNBT(IntTag intTag) {
        seconds = intTag.getAsInt();
    }

    @Override
    public boolean shouldSendUpdate() {
        if (isUpdateForced) {
            isUpdateForced = false;
            return true;
        }
        return false;
    }

    @Override
    public IntTag serializeOnUpdate() {
        LOGGER.info("{}: serialize on update", this);
        return IntTag.valueOf(seconds);
    }

    @Override
    public void deserializeOnUpdate(IntTag tag) {
        LOGGER.info("{}: deserialize on update", this);
        seconds = tag.getAsInt();
    }

    @Override
    public void subscribeEvents(IEventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.subscribe(SERVER_TICK.get(), this::onTick);
        eventManager.subscribe(TICK_SECOND.get(), this::onTickSecond);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
        if (cap == TEST_CAPABILITY.get()) {
            return testCap.cast();
        } else if (cap == ITEM_HANDLER_CAPABILITY.get()) {
            return itemCap.cast();
        }
        return LazyOptional.empty();
    }
}
