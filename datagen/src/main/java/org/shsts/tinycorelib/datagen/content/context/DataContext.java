package org.shsts.tinycorelib.datagen.content.context;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DataContext<D extends DataProvider> implements IDataContext<D> {
    public final String modid;
    public final D provider;

    public DataContext(String modid, D provider) {
        this.modid = modid;
        this.provider = provider;
    }

    @Override
    public String modid() {
        return modid;
    }

    @Override
    public D provider() {
        return provider;
    }
}
