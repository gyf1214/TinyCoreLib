package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IOnUseArg;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.registrate.IEntry;
import org.shsts.tinycorelib.api.registrate.IEntryHandler;

import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_REGISTRY_KEY;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_LOAD_LOC;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_USE_LOC;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class All {
    public static final IEntry<Block> TEST_BLOCK1;
    public static final IEntry<TestBlock> TEST_BLOCK2;
    public static final IEntry<TestItem> TEST_ITEM;

    public static final IEntryHandler<IEvent<?>> EVENTS;
    public static final IEntry<IEvent<Level>> SERVER_LOAD;
    public static final IEntry<IReturnEvent<IOnUseArg, InteractionResult>> SERVER_USE;

    static {
        TEST_BLOCK1 = REGISTRATE.block("test_block1", Block::new)
            .material(Material.DIRT)
            .noBlockItem()
            .register();

        TEST_BLOCK2 = REGISTRATE.block("test_block2", TestBlock::new)
            .properties(p -> p.strength(5f).requiresCorrectToolForDrops())
            .tint(0xFF00FFFF)
            .translucent()
            .blockItem()
            .properties(p -> p.tab(CreativeModeTab.TAB_DECORATIONS))
            .build()
            .register();

        TEST_ITEM = REGISTRATE.item("test_item", TestItem::new)
            .properties(p -> p.tab(CreativeModeTab.TAB_COMBAT))
            .tint(0xFFFFFF00)
            .register();

        EVENTS = REGISTRATE.entryHandler(EVENT_REGISTRY_KEY, IEvent.class);
        SERVER_LOAD = EVENTS.getEntry(SERVER_LOAD_LOC);
        SERVER_USE = EVENTS.getEntry(SERVER_USE_LOC);
    }

    public static void init() {}
}
