package org.shsts.tinycorelib.datagen.api.context;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataContext<D extends DataProvider> {
    String modid();

    D provider();
}
