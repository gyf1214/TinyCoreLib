package org.shsts.tinycorelib.datagen;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.Mod;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.ITinyDataGen;
import org.shsts.tinycorelib.datagen.content.DataGen;
import org.slf4j.Logger;

@Mod(ITinyDataGen.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyDataGen implements ITinyDataGen {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TinyDataGen() {
        LOGGER.info("Construct TinyDataGen");
    }

    @Override
    public IDataGen dataGen(String modid) {
        return new DataGen(modid);
    }
}
