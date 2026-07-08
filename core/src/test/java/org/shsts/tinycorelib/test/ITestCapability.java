package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITestCapability {
    void foo();

    int getSeconds();
}
