package org.shsts.tinycorelib.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.ModList;
import org.shsts.tinycorelib.api.registrate.IRegistrate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITinyCoreLib {
    String ID = "tinycorelib";

    IRegistrate registrate(String modid);

    static ITinyCoreLib get() {
        return (ITinyCoreLib) ModList.get().getModObjectById(ID).orElseThrow();
    }
}
