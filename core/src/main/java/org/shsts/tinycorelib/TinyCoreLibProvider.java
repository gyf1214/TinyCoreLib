package org.shsts.tinycorelib;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.ITinyCoreLib;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLibProvider implements Supplier<ITinyCoreLib> {
    private static TinyCoreLib INSTANCE = null;
    private static final Object lock = new Object();

    public static TinyCoreLib getCore() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = new TinyCoreLib();
            }
        }
        return INSTANCE;
    }

    @Override
    public ITinyCoreLib get() {
        return getCore();
    }
}
