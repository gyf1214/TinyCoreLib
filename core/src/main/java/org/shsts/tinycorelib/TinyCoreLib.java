package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.content.CoreContents;
import org.shsts.tinycorelib.content.ForgeEvents;
import org.shsts.tinycorelib.content.network.Channel;
import org.shsts.tinycorelib.content.recipe.SmartRecipeManager;
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
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
    }

    private void onConstructClient() {
        REGISTRATE.registerClient(modEventBus);
    }

    @Override
    public IRegistrate registrate(String modid) {
        return new Registrate(modid);
    }

    @Override
    public IChannel createChannel(ResourceLocation loc, String version) {
        return new Channel(loc, version);
    }

    @Override
    public IRecipeManager recipeManager(Level world) {
        return new SmartRecipeManager(world.getRecipeManager());
    }

    @Override
    public IRecipeManager clientRecipeManager() {
        var connection = Minecraft.getInstance().getConnection();
        assert connection != null;
        return new SmartRecipeManager(connection.getRecipeManager());
    }
}
