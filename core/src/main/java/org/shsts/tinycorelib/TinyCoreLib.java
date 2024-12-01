package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.content.CoreContents;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.content.CoreContents.REGISTRATE;

@Mod(ITinyCoreLib.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLib implements ITinyCoreLib {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final IEventBus modEventBus;

    public TinyCoreLib() {
        LOGGER.info("Construct TinyCoreLib!");
        this.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onConstruct);
    }

    private void onConstruct(FMLConstructModEvent event) {
        CoreContents.init();

        REGISTRATE.register(modEventBus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onConstructClient);
    }

    private void onConstructClient() {
        REGISTRATE.registerClient(modEventBus);
    }

    @Override
    public IRegistrate registrate(String modid) {
        return new Registrate(modid);
    }
}
