package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.BlockEntityTypeBuilder;
import org.shsts.tinycorelib.content.registrate.entry.BlockEntityTypeEntry;

import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityTypeHandler extends EntryHandler<BlockEntityType<?>> {
    private final Map<ResourceLocation, BlockEntityTypeEntry> typeEntries = new HashMap<>();

    public BlockEntityTypeHandler(Registrate registrate) {
        super(registrate, Registries.BLOCK_ENTITY_TYPE, BuiltInRegistries.BLOCK_ENTITY_TYPE);
    }

    public BlockEntityTypeEntry getTypeEntry(ResourceLocation loc) {
        return typeEntries.computeIfAbsent(loc,
            key -> new BlockEntityTypeEntry(key, () -> getEntry(key).get()));
    }

    public BlockEntityTypeEntry getTypeEntry(String id) {
        return getTypeEntry(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public BlockEntityTypeEntry registerType(BlockEntityTypeBuilder<?> builder) {
        builders.add(builder);
        return getTypeEntry(builder.loc());
    }
}
