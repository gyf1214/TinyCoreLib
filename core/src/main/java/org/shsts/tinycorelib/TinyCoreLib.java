package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.Mod;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.slf4j.Logger;

@Mod(ITinyCoreLib.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLib implements ITinyCoreLib {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TinyCoreLib() {
        LOGGER.info("Construct TinyCoreLib!");
    }

    @Override
    public IRegistrate registrate(String modid) {
        return new Registrate(modid);
    }
}
