package org.shsts.tinycorelib.datagen.content.context;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DataContext<P extends DataProvider> implements IDataContext<P> {
    public final String modid;
    public final P provider;

    public DataContext(String modid, P provider) {
        this.modid = modid;
        this.provider = provider;
    }

    @Override
    public String modid() {
        return modid;
    }

    @Override
    public P provider() {
        return provider;
    }
}
