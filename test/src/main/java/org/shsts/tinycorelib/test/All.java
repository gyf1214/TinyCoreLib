package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.Material;
import org.shsts.tinycorelib.api.registrate.IEntry;

import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class All {
    public static final IEntry<TestBlock> TEST_BLOCK1;
    public static final IEntry<TestBlock> TEST_BLOCK2;
    public static final IEntry<TestItem> TEST_ITEM;

    static {
        TEST_BLOCK1 = REGISTRATE.block("test1", TestBlock::new)
            .material(Material.DIRT)
            .noBlockItem()
            .register();

        TEST_BLOCK2 = REGISTRATE.block("test2", TestBlock::new)
            .properties(p -> p.strength(5f))
            .tint(0xFF00FFFF)
            .translucent()
            .register();

        TEST_ITEM = REGISTRATE.item("test", TestItem::new)
            .properties(p -> p.tab(CreativeModeTab.TAB_COMBAT))
            .tint(0xFFFFFF00)
            .register();
    }

    public static void init() {}
}
