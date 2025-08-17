package org.shsts.tinycorelib.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.IRegistrate;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITinyCoreLib {
    String ID = "tinycorelib";
    String PROVIDER_CLASS = "org.shsts.tinycorelib.TinyCoreLibProvider";

    IRegistrate registrate(String modid);

    IMetaExecutor registerMeta(String folder, IMetaConsumer consumer);

    IChannel createChannel(ResourceLocation loc, String version);

    IRecipeManager recipeManager(Level world);

    IRecipeManager clientRecipeManager();

    static ITinyCoreLib get() {
        try {
            var provider = (Supplier<?>) Class.forName(PROVIDER_CLASS).getConstructor().newInstance();
            return (ITinyCoreLib) provider.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
