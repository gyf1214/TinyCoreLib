package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
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

    public TinyCoreLibMod(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
        this.core = TinyCoreLibProvider.getCore();

        modEventBus.addListener(this::onConstruct);
    }

    private void onConstruct(FMLConstructModEvent event) {
        LOGGER.info("Construct TinyCoreLib!");
        core.scanMeta();
        CoreContents.init();

        REGISTRATE.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            onConstructClient();
        }
        NeoForge.EVENT_BUS.register(ForgeEvents.class);
    }

    private void onConstructClient() {
        REGISTRATE.registerClient(modEventBus);
    }
}
