package org.shsts.tinycorelib.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.registrate.IRegistrate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITinyCoreLib {
    String ID = "tinycorelib";

    IRegistrate registrate(String modid);

    IChannel createChannel(ResourceLocation loc, String version);

    static ITinyCoreLib get() {
        return (ITinyCoreLib) ModList.get().getModObjectById(ID).orElseThrow();
    }
}
