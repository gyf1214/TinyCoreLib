package org.shsts.tinycorelib.datagen.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.registrate.IRegistrate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITinyDataGen {
    String ID = "tinydatagen";
    String PROVIDER_CLASS = "org.shsts.tinycorelib.datagen.TinyDataGen";

    IDataGen dataGen(IRegistrate registrate);

    IDataGen dataGen(IRegistrate registrate, boolean failValidation);

    static ITinyDataGen get() {
        try {
            return (ITinyDataGen) Class.forName(PROVIDER_CLASS).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
