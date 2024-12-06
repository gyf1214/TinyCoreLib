package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.gui.IMenuEvent;
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

    public static final IRecipeType<TestRecipe.Builder> TEST_RECIPE;
    public static final IRecipeType<TestVanillaRecipe.Builder> TEST_VANILLA_RECIPE;

    public static final ResourceKey<Biome> VOID_BIOME;
    public static final IEntry<VoidPreset> VOID_PRESET;

    static {
        CHANNEL = CORE.createChannel(new ResourceLocation(TinyCoreLibTest.ID, "channel"), "1");

        TEST_MENU_EVENT = CHANNEL
            .registerMenuSyncPacket(TestPacket.class, TestPacket::new)
            .registerMenuEventPacket(TestPacket.class, TestPacket::new);

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

        ITEM_HANDLER_CAPABILITY = REGISTRATE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

        TEST_MENU = REGISTRATE.setDefaultChannel(CHANNEL)
            .menu("test_menu")
            .title($ -> new TextComponent("Test Title"))
            .dummyPlugin(menu -> menu.addSyncSlot("seconds", TestPacket::new))
            .plugin(TestMenuPlugin::new)
            .screen(() -> () -> TestScreen::new)
            .register();

        EVENTS = REGISTRATE.getHandler(EVENT_REGISTRY_KEY, IEvent.class);
        SERVER_TICK = EVENTS.getEntry(SERVER_TICK_LOC);
        TICK_SECOND = REGISTRATE.event("tick_second");

        TEST_RECIPE = REGISTRATE.recipeType("test", TestRecipe.Builder::new)
            .recipeClass(TestRecipe.class)
            .serializer(TestRecipe.SERIALIZER)
            .register();

        TEST_VANILLA_RECIPE = REGISTRATE.vanillaRecipeType("test_vanilla", TestVanillaRecipe.Builder::new)
            .recipeClass(TestVanillaRecipe.class)
            .serializer(TestVanillaRecipe.SERIALIZER)
            .register();

        VOID_BIOME = REGISTRATE.createDynamicHandler(ForgeRegistries.BIOMES, OverworldBiomes::theVoid)
            .dynamicEntry(ForgeRegistries.BIOMES, "void");

        var presetHandler = REGISTRATE.getHandler(ForgeRegistries.Keys.WORLD_TYPES, ForgeWorldPreset.class);
        VOID_PRESET = REGISTRATE.registryEntry(presetHandler, "void", VoidPreset::new);
    }

    public static void init() {}
}
