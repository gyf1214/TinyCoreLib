package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LootTableHandler extends DataHandler<LootTableProvider> {
    private final List<BlockLootEntry> blockLootEntries = new ArrayList<>();

    public LootTableHandler(DataGen dataGen) {
        super(dataGen);
    }

    private record BlockLootEntry(ResourceKey<LootTable> key,
        Function<HolderLookup.Provider, LootTable.Builder> factory) {}

    private static ResourceKey<LootTable> blockLootKey(ResourceLocation loc) {
        return ResourceKey.create(Registries.LOOT_TABLE, loc.withPrefix("blocks/"));
    }

    private static LootTable.Builder dropTable(ItemLike item, float chance) {
        var pool = LootPool.lootPool()
            .when(ExplosionCondition.survivesExplosion())
            .add(LootItem.lootTableItem(item));
        if (chance < 1f) {
            pool.when(LootItemRandomChanceCondition.randomChance(chance));
        }
        return LootTable.lootTable().withPool(pool);
    }

    private static LootTable.Builder dropOnStateTable(ItemLike item, Block block,
        StatePropertiesPredicate.Builder predicate) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
            .when(ExplosionCondition.survivesExplosion())
            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(predicate))
            .add(LootItem.lootTableItem(item)));
    }

    private static LootTable.Builder dropOnToolTable(HolderLookup.Provider holderLookup,
        ItemLike item, TagKey<Item> tool) {
        var silkTouch = holderLookup.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SILK_TOUCH);
        var pool = LootPool.lootPool()
            .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(tool))
                .or(MatchTool.toolMatches(ItemPredicate.Builder.item()
                    .withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
                        ItemEnchantmentsPredicate.enchantments(List.of(
                            new EnchantmentPredicate(silkTouch,
                                MinMaxBounds.Ints.atLeast(1))))))))
            .add(LootItem.lootTableItem(item));
        return LootTable.lootTable().withPool(pool);
    }

    @Override
    public LootTableProvider createProvider(GatherDataEvent event) {
        return new LootTableProvider(
            event.getGenerator().getPackOutput(),
            Set.of(),
            List.of(new LootTableProvider.SubProviderEntry(
                holderLookup -> output -> {
                    for (var entry : blockLootEntries) {
                        output.accept(entry.key(), entry.factory().apply(holderLookup));
                    }
                },
                LootContextParamSets.BLOCK)),
            event.getLookupProvider());
    }

    public void drop(ResourceLocation loc, ItemLike item, float chance) {
        blockLootEntries.add(new BlockLootEntry(blockLootKey(loc),
            $ -> dropTable(item, chance)));
    }

    public <V extends Comparable<V> & StringRepresentable> void dropOnState(
        ResourceLocation loc, ItemLike item, Block block, Property<V> prop, V value) {
        blockLootEntries.add(new BlockLootEntry(blockLootKey(loc),
            $ -> dropOnStateTable(item, block,
                StatePropertiesPredicate.Builder.properties().hasProperty(prop, value))));
    }

    public void dropOnState(ResourceLocation loc, ItemLike item,
        Block block, BooleanProperty prop, boolean value) {
        blockLootEntries.add(new BlockLootEntry(blockLootKey(loc),
            $ -> dropOnStateTable(item, block,
                StatePropertiesPredicate.Builder.properties().hasProperty(prop, value))));
    }

    public void dropOnTool(ResourceLocation loc, ItemLike item, TagKey<Item> tool) {
        blockLootEntries.add(new BlockLootEntry(blockLootKey(loc),
            holderLookup -> dropOnToolTable(holderLookup, item, tool)));
    }
}
