package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TintHandler {
    private final Map<Block, BlockColor> blockColors = new HashMap<>();
    private final Map<Item, ItemColor> itemColors = new HashMap<>();

    public void addBlockColor(Block block, BlockColor blockColor) {
        blockColors.put(block, blockColor);
    }

    public void addItemColor(Item item, ItemColor itemColor) {
        itemColors.put(item, itemColor);
    }

    public void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        for (var entry : blockColors.entrySet()) {
            event.register(entry.getValue(), entry.getKey());
        }
        blockColors.clear();
    }

    public void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        for (var entry : itemColors.entrySet()) {
            event.register(entry.getValue(), entry.getKey());
        }
        itemColors.clear();
    }
}
