package org.shsts.tinycorelib.datagen.content.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.datagen.api.builder.IDataBuilder;
import org.shsts.tinycorelib.datagen.content.DataGen;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class DataBuilder<P, S extends IDataBuilder<P, S>>
    extends Builder<Unit, P, S> implements IDataBuilder<P, S> {
    protected final DataGen dataGen;
    public final ResourceLocation loc;

    public DataBuilder(DataGen dataGen, P parent, ResourceLocation loc) {
        super(parent);
        this.dataGen = dataGen;
        this.loc = loc;
        onBuild.add(this::register);
    }

    protected abstract void register();

    @Override
    protected Unit createObject() {
        return Unit.INSTANCE;
    }

    @Override
    public ResourceLocation buildLoc() {
        build();
        return loc;
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }
}
