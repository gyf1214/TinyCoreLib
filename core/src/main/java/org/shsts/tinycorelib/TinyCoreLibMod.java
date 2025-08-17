package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.content.CoreContents;
import org.shsts.tinycorelib.content.ForgeEvents;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.content.CoreContents.REGISTRATE;

@Mod(ITinyCoreLib.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLibMod {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final TinyCoreLib core;
    private final IEventBus modEventBus;

    public TinyCoreLibMod() {
        this.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        this.core = TinyCoreLibProvider.getCore();

        modEventBus.addListener(this::onConstruct);
    }

    private void onConstruct(FMLConstructModEvent event) {
        LOGGER.info("Construct TinyCoreLib!");
        core.scanMeta();
        CoreContents.init();

        REGISTRATE.register(modEventBus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onConstructClient);
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
    }

    private void onConstructClient() {
        REGISTRATE.registerClient(modEventBus);
    }
}
