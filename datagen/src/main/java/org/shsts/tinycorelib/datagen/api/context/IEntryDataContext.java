package org.shsts.tinycorelib.datagen.api.context;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.api.core.ILoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IEntryDataContext<T extends IForgeRegistryEntry<T>,
    U extends T, P extends DataProvider> extends ILoc, IDataContext<P> {
    @Override
    String modid();

    U object();
}
