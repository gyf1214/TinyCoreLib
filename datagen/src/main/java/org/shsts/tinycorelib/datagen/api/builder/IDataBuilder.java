package org.shsts.tinycorelib.datagen.api.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.core.ILoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDataBuilder<P, S extends IDataBuilder<P, S>> extends ILoc, IBuilder<Unit, P, S> {
    ResourceLocation buildLoc();
}
