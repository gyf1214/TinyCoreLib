package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLEnvironment;
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

    public TinyCoreLibTest(IEventBus modEventBus) {
        LOGGER.info("Construct TinyCoreLib Test!");
        this.modEventBus = modEventBus;
        modEventBus.addListener(this::onConstructEvent);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onConstructEvent(FMLConstructModEvent event) {
        this.onConstruct();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            onConstructClient();
        }
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
