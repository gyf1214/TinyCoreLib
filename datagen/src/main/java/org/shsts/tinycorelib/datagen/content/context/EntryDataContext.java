package org.shsts.tinycorelib.datagen.content.context;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntryDataContext<T extends IForgeRegistryEntry<T>,
    U extends T, D extends DataProvider> extends DataContext<D> implements IEntryDataContext<T, U, D> {
    public final ResourceLocation loc;
    public final U object;

    public EntryDataContext(String modid, String id, D provider, U object) {
        super(modid, provider);
        this.loc = new ResourceLocation(modid, id);
        this.object = object;
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    @Override
    public U object() {
        return object;
    }
}
