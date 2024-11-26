package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.ModList;
import org.shsts.tinycorelib.api.registrate.IRegistrate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITinyDataGen {
    String ID = "tinydatagen";

    IDataGen dataGen(IRegistrate registrate);

    static ITinyDataGen get() {
        return (ITinyDataGen) ModList.get().getModObjectById(ID).orElseThrow();
    }
}
