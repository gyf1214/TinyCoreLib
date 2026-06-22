package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.gui.IMenuEvent;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;

import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_REGISTRY_KEY;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_TICK_LOC;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.CORE;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class All {
    public static final IChannel CHANNEL;

    public static final IMetaExecutor TEST_META;

    public static final IEntry<Block> TEST_BLOCK1;
    public static final IEntry<TestBlock> TEST_BLOCK2;
    public static final IEntry<TestEntityBlock> TEST_BLOCK3;
    public static final IEntry<TestItem> TEST_ITEM;
    public static final IBlockEntityType TEST_BLOCK_ENTITY;
    public static final IMenuType TEST_MENU;

    public static final ICapability<ITestCapability> TEST_CAPABILITY;
    public static final ICapability<IItemHandler> ITEM_HANDLER_CAPABILITY;

    public static final IEntryHandler<IEvent<?>> EVENTS;
    public static final IEntry<IEvent<Level>> SERVER_TICK;
    public static final IEntry<IEvent<Unit>> TICK_SECOND;

    public static final IMenuEvent<TestPacket> TEST_MENU_EVENT;

    public static final IRecipeType<TestRecipe> TEST_RECIPE;
    public static final IRecipeType<TestCookingRecipe> TEST_COOKING_RECIPE;

    static {
        CHANNEL = CORE.createChannel(ResourceLocation.fromNamespaceAndPath(TinyCoreLibTest.ID, "channel"), "1");

        TEST_META = CORE.registerMeta("test", new TestMetaConsumer());

        TEST_MENU_EVENT = CHANNEL
            .registerMenuSyncPacket(TestPacket.class, TestPacket::new)
            .registerMenuEventPacket(TestPacket.class, TestPacket::new);

        TEST_BLOCK1 = REGISTRATE.block("test_block1", Block::new)
            .properties(p -> p.mapColor(MapColor.DIRT))
            .noBlockItem()
            .register();

        TEST_BLOCK2 = REGISTRATE.block("test_block2", TestBlock::new)
            .properties(p -> p.mapColor(MapColor.STONE).strength(5f).requiresCorrectToolForDrops())
            .tint(0xFF00FFFF)
            .blockItem()
            .creativeTab(CreativeModeTabs.BUILDING_BLOCKS)
            .end()
            .register();

        TEST_BLOCK3 = REGISTRATE.block("test_block3", TestEntityBlock::new)
            .properties(p -> p.mapColor(MapColor.STONE))
            .register();

        TEST_ITEM = REGISTRATE.item("test_item", TestItem::new)
            .creativeTab(CreativeModeTabs.COMBAT)
            .tint(0xFFFFFF00)
            .register();

        TEST_CAPABILITY = REGISTRATE.capability("test_capability", ITestCapability.class);

        ITEM_HANDLER_CAPABILITY = REGISTRATE.capability(Capabilities.ItemHandler.BLOCK);

        TEST_BLOCK_ENTITY = REGISTRATE.blockEntityType("test_block_entity")
            .validBlock(TEST_BLOCK3)
            .capability(TEST_CAPABILITY, ITEM_HANDLER_CAPABILITY)
            .container("test_capability", TestCapability::new)
            .register();

        TEST_MENU = REGISTRATE.setDefaultChannel(CHANNEL)
            .menu("test_menu", TestMenu::new)
            .title($ -> Component.literal("Test Title"))
            .screen(() -> () -> TestScreen::new)
            .register();

        EVENTS = REGISTRATE.getHandler(EVENT_REGISTRY_KEY, IEvent.class);
        SERVER_TICK = EVENTS.getEntry(SERVER_TICK_LOC);
        TICK_SECOND = REGISTRATE.event("tick_second");

        TEST_RECIPE = REGISTRATE.<TestRecipe>recipeType("test")
            .recipeClass(TestRecipe.class)
            .serializer(TestRecipe.CODEC)
            .register();

        TEST_COOKING_RECIPE = REGISTRATE.<TestCookingRecipe>recipeType("test_cooking")
            .recipeClass(TestCookingRecipe.class)
            .serializer(TestCookingRecipe.CODEC)
            .register();
    }

    public static void init() {}

}
