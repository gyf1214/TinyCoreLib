package org.shsts.tinycorelib.datagen.content.handler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import net.minecraft.data.PackOutput;
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LootTableHandler extends DataHandler<LootTableHandler.Provider> {
    public LootTableHandler(DataGen dataGen) {
        super(dataGen);
    }

    private record LootEntry(ResourceLocation loc, Function<HolderLookup.Provider, LootPool.Builder> factory) {}

    public class Provider extends LootTableProvider {
        private final Multimap<LootContextParamSet, LootEntry> entries = ArrayListMultimap.create();

        public Provider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, Set.of(), List.of(), registries);
        }

        public void addBlock(ResourceLocation loc, Function<HolderLookup.Provider, LootPool.Builder> factory) {
            entries.put(LootContextParamSets.BLOCK, new LootEntry(loc.withPrefix("blocks/"), factory));
        }

        public void addBlock(ResourceLocation loc, LootPool.Builder pool) {
            addBlock(loc, $ -> pool);
        }

        private SubProviderEntry createTable(LootContextParamSet paramSet) {
            var tableEntries = entries.get(paramSet);
            return new LootTableProvider.SubProviderEntry(provider -> output -> {
                var tables = new LinkedHashMap<ResourceLocation, LootTable.Builder>();
                for (var entry : tableEntries) {
                    tables.computeIfAbsent(entry.loc, $ -> LootTable.lootTable())
                        .withPool(entry.factory.apply(provider));
                }
                tables.forEach((loc, table) -> output.accept(
                    ResourceKey.create(Registries.LOOT_TABLE, loc), table));
            }, paramSet);
        }

        @Override
        public List<SubProviderEntry> getTables() {
            register(this);
            return entries.keySet().stream()
                .map(this::createTable)
                .toList();
        }
    }

    @Override
    public Provider createProvider(GatherDataEvent event) {
        return new Provider(event.getGenerator().getPackOutput(), event.getLookupProvider());
    }

    private static LootPool.Builder dropPool(ItemLike item, float chance) {
        var pool = LootPool.lootPool()
            .when(ExplosionCondition.survivesExplosion())
            .add(LootItem.lootTableItem(item));
        if (chance < 1f) {
            pool.when(LootItemRandomChanceCondition.randomChance(chance));
        }
        return pool;
    }

    public void drop(ResourceLocation loc, ItemLike item, float chance) {
        addCallback($ -> $.addBlock(loc, dropPool(item, chance)));
    }

    private static LootPool.Builder dropOnStatePool(ItemLike item, Block block,
        StatePropertiesPredicate.Builder predicate) {
        return LootPool.lootPool()
            .when(ExplosionCondition.survivesExplosion())
            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(predicate))
            .add(LootItem.lootTableItem(item));
    }

    public <V extends Comparable<V> & StringRepresentable> void dropOnState(
        ResourceLocation loc, ItemLike item, Block block, Property<V> prop, V value) {
        addCallback($ -> $.addBlock(loc, dropOnStatePool(item, block,
            StatePropertiesPredicate.Builder.properties().hasProperty(prop, value))));
    }

    public void dropOnState(ResourceLocation loc, ItemLike item,
        Block block, BooleanProperty prop, boolean value) {
        addCallback($ -> $.addBlock(loc, dropOnStatePool(item, block,
            StatePropertiesPredicate.Builder.properties().hasProperty(prop, value))));
    }

    private static LootPool.Builder dropOnToolPool(HolderLookup.Provider holderLookup,
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
        return pool;
    }

    public void dropOnTool(ResourceLocation loc, ItemLike item, TagKey<Item> tool) {
        addCallback($ -> $.addBlock(loc, holderLookup -> dropOnToolPool(holderLookup, item, tool)));
    }
}
