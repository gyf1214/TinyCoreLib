package org.shsts.tinycorelib.test.datagen;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.ITinyDataGen;
import org.shsts.tinycorelib.test.TinyCoreLibTest;
import org.slf4j.Logger;

@Mod(TinyDataGenTest.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyDataGenTest {
    public static final String ID = "tinydatagen_test";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ITinyDataGen DATA_CORE;
    public static IDataGen DATA_GEN;

    public TinyDataGenTest() {
        LOGGER.info("Construct TinyDataGen Test!");
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event) {
        DATA_CORE = ITinyDataGen.get();
        DATA_GEN = DATA_CORE.dataGen(TinyCoreLibTest.REGISTRATE);

        AllDataGen.init();

        DATA_GEN.onGatherData(event);
    }
}
