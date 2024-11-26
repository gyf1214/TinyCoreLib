package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.registrate.IEntry;

import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class All {
    public static final IEntry<TestItem> TEST_ITEM;

    static {
        TEST_ITEM = REGISTRATE.item("test", TestItem::new)
            .tint(0xFFFFFF00)
            .register();
    }

    public static void init() {}
}
