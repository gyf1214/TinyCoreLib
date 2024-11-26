package org.shsts.tinycorelib.api.core;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ILoc {
    ResourceLocation loc();

    default String id() {
        return loc().getPath();
    }

    default String modid() {
        return loc().getNamespace();
    }
}
