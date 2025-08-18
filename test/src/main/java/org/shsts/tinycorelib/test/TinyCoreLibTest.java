package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.SERVER_TICK;
import static org.shsts.tinycorelib.test.All.TEST_META;

@Mod(TinyCoreLibTest.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLibTest {
    public static final String ID = "tinycorelib_test";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ITinyCoreLib CORE;
    public static IRegistrate REGISTRATE;

    private final IEventBus modEventBus;

    public TinyCoreLibTest() {
        LOGGER.info("Construct TinyCoreLib Test!");
        this.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onConstructEvent);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onConstructEvent(FMLConstructModEvent event) {
        this.onConstruct();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onConstructClient);
    }

    private void onConstruct() {
        CORE = ITinyCoreLib.get();
        REGISTRATE = CORE.registrate(ID);

        All.init();

        TEST_META.execute();
        REGISTRATE.register(modEventBus);
    }

    private void onConstructClient() {
        REGISTRATE.registerClient(modEventBus);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Server Load Event = {}", SERVER_TICK.get());
    }
}
