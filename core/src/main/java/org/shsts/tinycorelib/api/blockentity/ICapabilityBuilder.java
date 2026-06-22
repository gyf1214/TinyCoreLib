package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ICapabilityBuilder {
    <T> void attach(ICapability<T> capability, T value);
}
