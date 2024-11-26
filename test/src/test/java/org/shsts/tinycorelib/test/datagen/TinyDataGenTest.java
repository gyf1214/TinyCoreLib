package org.shsts.tinycorelib.test.datagen;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.ITinyDataGen;
import org.slf4j.Logger;

@Mod(TinyDataGenTest.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyDataGenTest {
    public static final String ID = "tinydatagen_test";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ITinyDataGen CORE;
    public static IDataGen DATA_GEN;

    public TinyDataGenTest() {
        LOGGER.info("Construct TinyDataGen Test!");
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onConstructEvent);
        modEventBus.addListener(this::onGatherData);
    }

    private void onConstructEvent(FMLConstructModEvent event) {
        event.enqueueWork(this::onConstruct);
    }

    private void onConstruct() {
        CORE = (ITinyDataGen) ModList.get().getModObjectById(ITinyDataGen.ID).orElseThrow();
        DATA_GEN = CORE.dataGen(ID);
    }

    private void onGatherData(GatherDataEvent event) {
        AllDataGen.init();
        DATA_GEN.onGatherData(event);
    }
}
