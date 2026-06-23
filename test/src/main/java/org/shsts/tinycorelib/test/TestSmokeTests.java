package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(TinyCoreLibTest.ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestSmokeTests {
    @GameTest
    public static void smoke(GameTestHelper helper) {
        helper.succeed();
    }
}
