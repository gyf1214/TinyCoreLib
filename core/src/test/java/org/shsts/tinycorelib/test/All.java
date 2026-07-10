package org.shsts.tinycorelib.test;

import com.mojang.serialization.Codec;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.network.PacketDirection;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.api.registrate.handler.IEntryHandler;

import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_MANAGER_LOC;
import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_REGISTRY_KEY;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_TICK_LOC;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.CORE;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class All {
    public static final ICapability<IEventManager> EVENT_MANAGER;
    public static final ICapability<ITestCapability> TEST_CAPABILITY;
    public static final ICapability<IItemHandler> ITEM_HANDLER_CAPABILITY;
    public static final IItemCapability<ITestCapability> TEST_ITEM_CAPABILITY;
    public static final IEntryHandler<DataComponentType<?>> DATA_COMPONENT;
    public static final IEntry<DataComponentType<Integer>> TEST_ITEM_COMPONENT;

    public static final IMetaExecutor TEST_META;

    public static final IEntry<Block> TEST_BLOCK1;
    public static final IEntry<TestBlock> TEST_BLOCK2;
    public static final IEntry<TestEntityBlock> TEST_BLOCK3;
    public static final IEntry<TestItem> TEST_ITEM;
    public static final IBlockEntityType TEST_BLOCK_ENTITY;
    public static final IMenuType TEST_MENU;

    public static final IEntryHandler<IEvent<?>> EVENTS;
    public static final IEntry<IEvent<Level>> SERVER_TICK;
    public static final IEntry<IEvent<Unit>> TICK_SECOND;

    public static final IPacketType<TestPacket> TEST_PACKET;
    public static final IPacketType<TestPacket> TEST_MENU_SYNC;
    public static final IPacketType<TestPacket> TEST_MENU_EVENT;

    public static final IRecipeType<TestRecipe> TEST_RECIPE;
    public static final IRecipeType<TestCookingRecipe> TEST_COOKING_RECIPE;

    static {
        EVENT_MANAGER = REGISTRATE.getCapability(EVENT_MANAGER_LOC, IEventManager.class);
        TEST_CAPABILITY = REGISTRATE.capability("test_capability", ITestCapability.class);
        ITEM_HANDLER_CAPABILITY = REGISTRATE.getCapability(Capabilities.ItemHandler.BLOCK);
        TEST_ITEM_CAPABILITY = REGISTRATE.itemCapability("test_capability", ITestCapability.class);

        DATA_COMPONENT = REGISTRATE.getHandler(Registries.DATA_COMPONENT_TYPE, BuiltInRegistries.DATA_COMPONENT_TYPE);
        TEST_ITEM_COMPONENT = REGISTRATE.registryEntry(DATA_COMPONENT, "test_item_component", () -> DataComponentType
            .<Integer>builder()
            .persistent(Codec.INT)
            .build());

        TEST_META = CORE.registerMeta("test", new TestMetaConsumer());

        TEST_PACKET = REGISTRATE.packet("test_packet", TestPacket::new)
            .direction(PacketDirection.BIDIRECTIONAL)
            .handler((packet, context) -> {})
            .register();
        TEST_MENU_SYNC = REGISTRATE.menuSyncPacket("test_menu_sync", TestPacket::new);
        TEST_MENU_EVENT = REGISTRATE.menuEventPacket("test_menu_event", TestPacket::new);

        TEST_BLOCK1 = REGISTRATE.block("test_block1", Block::new)
            .properties(p -> p.mapColor(MapColor.DIRT))
            .noBlockItem()
            .register();

        TEST_BLOCK2 = REGISTRATE.block("test_block2", TestBlock::new)
            .properties(p -> p.mapColor(MapColor.STONE).strength(5f).requiresCorrectToolForDrops())
            .tint(0xFF00FFFF)
            .creativeTab(CreativeModeTabs.BUILDING_BLOCKS)
            .register();

        TEST_BLOCK3 = REGISTRATE.block("test_block3", TestEntityBlock::new)
            .properties(p -> p.mapColor(MapColor.STONE))
            .register();

        TEST_ITEM = REGISTRATE.item("test_item", TestItem::new)
            .capability(TEST_ITEM_CAPABILITY)
            .creativeTab(CreativeModeTabs.COMBAT)
            .creativeTab(CreativeModeTabs.COMBAT, item -> {
                var ret = new ItemStack(item);
                ret.set(TEST_ITEM_COMPONENT, 10);
                return ret;
            })
            .itemProperty(TestItem.PROPERTY, () -> () -> (stack, $1, $2, $3) ->
                stack.getOrDefault(TEST_ITEM_COMPONENT, 0) / 10f)
            .tint(0xFFFFFF00)
            .register();

        TEST_BLOCK_ENTITY = REGISTRATE.blockEntityType("test_block_entity")
            .validBlock(TEST_BLOCK3)
            .capability(TEST_CAPABILITY, ITEM_HANDLER_CAPABILITY)
            .container("test_capability", TestContainer::new)
            .register();

        TEST_MENU = REGISTRATE.menu("test_menu", TestMenu::new)
            .title($ -> Component.literal("Test Title"))
            .screen(() -> () -> TestScreen::new)
            .register();

        EVENTS = REGISTRATE.getHandler(EVENT_REGISTRY_KEY);
        SERVER_TICK = EVENTS.getEntry(SERVER_TICK_LOC);
        TICK_SECOND = REGISTRATE.event("tick_second");

        TEST_RECIPE = REGISTRATE.recipeType("test", TestRecipe.class)
            .serializer(TestRecipe.CODEC)
            .register();

        TEST_COOKING_RECIPE = REGISTRATE.recipeType("test_cooking", TestCookingRecipe.class)
            .serializer(TestCookingRecipe.CODEC)
            .register();
    }

    public static void init() {}
}
