package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.shsts.tinycorelib.api.blockentity.ICapabilityBuilder;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IEventSubscriber;
import org.shsts.tinycorelib.api.blockentity.INBTUpdatable;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.ITEM_HANDLER_CAPABILITY;
import static org.shsts.tinycorelib.test.All.SERVER_TICK;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_COOKING_RECIPE;
import static org.shsts.tinycorelib.test.All.TEST_ITEM_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_RECIPE;
import static org.shsts.tinycorelib.test.All.TICK_SECOND;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.CORE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestContainer implements ICapabilityContainer, IEventSubscriber, ITestCapability,
    INBTSerializable<IntTag>, INBTUpdatable<IntTag> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final BlockEntity blockEntity;
    private final ItemStackHandler itemHandler;

    private IEventManager eventManager;
    private boolean isUpdateForced = true;
    private int ticks = 0;
    private int seconds = 0;

    public TestContainer(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.itemHandler = new ItemStackHandler(1);
    }

    @Override
    public void attachCapability(ICapabilityBuilder builder) {
        builder.attach(TEST_CAPABILITY, this);
        builder.attach(ITEM_HANDLER_CAPABILITY, itemHandler);
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
        var stack = itemHandler.getStackInSlot(0);
        TEST_ITEM_CAPABILITY.tryGet(stack).ifPresent(ITestCapability::foo);
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
        world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);

        var recipeManager = CORE.recipeManager(world);
        var recipes = recipeManager.getRecipesFor(TEST_RECIPE, this);
        for (var recipe : recipes) {
            LOGGER.info("matched test recipe = {}", recipe.loc());
        }
        recipeManager.getRecipeFor(TEST_COOKING_RECIPE, blockEntity)
            .ifPresent(recipe -> itemHandler.setStackInSlot(0, recipe.get().getResult()));
    }

    @Override
    public int getSeconds() {
        return seconds;
    }

    @Override
    public IntTag serializeNBT(HolderLookup.Provider provider) {
        return IntTag.valueOf(seconds);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, IntTag intTag) {
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
    public IntTag serializeOnUpdate(HolderLookup.Provider provider) {
        LOGGER.info("{}: serialize on update", this);
        return IntTag.valueOf(seconds);
    }

    @Override
    public void deserializeOnUpdate(HolderLookup.Provider provider, IntTag tag) {
        LOGGER.info("{}: deserialize on update", this);
        seconds = tag.getAsInt();
    }

    @Override
    public void subscribeEvents(IEventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.subscribe(SERVER_TICK.get(), this::onTick);
        eventManager.subscribe(TICK_SECOND.get(), this::onTickSecond);
    }
}
