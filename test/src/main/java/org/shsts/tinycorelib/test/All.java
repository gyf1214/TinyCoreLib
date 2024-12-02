package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.registrate.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.ICapability;
import org.shsts.tinycorelib.api.registrate.IEntry;
import org.shsts.tinycorelib.api.registrate.IEntryHandler;

import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_REGISTRY_KEY;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_TICK_LOC;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class All {
    public static final IEntry<Block> TEST_BLOCK1;
    public static final IEntry<TestBlock> TEST_BLOCK2;
    public static final IEntry<TestEntityBlock> TEST_BLOCK3;
    public static final IEntry<TestItem> TEST_ITEM;
    public static final IBlockEntityType TEST_BLOCK_ENTITY;

    public static final ICapability<ITestCapability> TEST_CAPABILITY;

    public static final IEntryHandler<IEvent<?>> EVENTS;
    public static final IEntry<IEvent<Level>> SERVER_TICK;
    public static final IEntry<IEvent<Unit>> TICK_SECOND;

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

        TEST_BLOCK3 = REGISTRATE.block("test_block3", TestEntityBlock::new)
            .register();

        TEST_ITEM = REGISTRATE.item("test_item", TestItem::new)
            .properties(p -> p.tab(CreativeModeTab.TAB_COMBAT))
            .tint(0xFFFFFF00)
            .register();

        TEST_BLOCK_ENTITY = REGISTRATE.blockEntityType("test_block_entity")
            .validBlock(TEST_BLOCK3)
            .capability("test_capability", TestCapability::new)
            .register();

        TEST_CAPABILITY = REGISTRATE.capability(ITestCapability.class, new CapabilityToken<>() {});

        EVENTS = REGISTRATE.getHandler(EVENT_REGISTRY_KEY, IEvent.class);
        SERVER_TICK = EVENTS.getEntry(SERVER_TICK_LOC);
        TICK_SECOND = REGISTRATE.event("tick_second");
    }

    public static void init() {}
}
