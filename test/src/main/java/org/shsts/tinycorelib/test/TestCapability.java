package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IEventSubscriber;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.SERVER_TICK;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TICK_SECOND;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestCapability implements ITestCapability, ICapabilityProvider, IEventSubscriber {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final LazyOptional<ITestCapability> myself;

    private IEventManager eventManager;
    private int ticks = 0;

    public TestCapability() {
        this.myself = LazyOptional.of(() -> this);
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
    }

    @Override
    public void subscribeEvents(IEventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.subscribe(SERVER_TICK.get(), this::onTick);
        eventManager.subscribe(TICK_SECOND.get(), this::onTickSecond);
    }

    @Override
    public void foo() {
        LOGGER.info("{}: moo!", this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
        if (cap == TEST_CAPABILITY.get()) {
            return myself.cast();
        }
        return LazyOptional.empty();
    }
}
