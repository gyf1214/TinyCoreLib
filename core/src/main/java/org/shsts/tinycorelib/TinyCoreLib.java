package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.content.meta.MetaExecutor;
import org.shsts.tinycorelib.content.meta.MetaLocator;
import org.shsts.tinycorelib.content.network.Channel;
import org.shsts.tinycorelib.content.recipe.SmartRecipeManager;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLib implements ITinyCoreLib {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final MetaLocator metaLocator = new MetaLocator();

    public TinyCoreLib() {
        LOGGER.info("Initialize TinyCoreLib API");
    }

    public void scanMeta() {
        metaLocator.scanFiles();
    }

    @Override
    public IRegistrate registrate(String modid) {
        return new Registrate(modid);
    }

    @Override
    public IMetaExecutor registerMeta(String folder, IMetaConsumer consumer) {
        return new MetaExecutor(metaLocator, folder, consumer);
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
