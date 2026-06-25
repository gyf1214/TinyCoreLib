package org.shsts.tinycorelib.content.gametest;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EmptyGameTestStructures {
    private static final Pattern NamePattern = Pattern.compile("^empty_(\\d+)x(\\d+)x(\\d+)$");
    private static final int MaxDimension = 64;

    private EmptyGameTestStructures() {}

    public static Optional<StructureTemplate> create(ResourceLocation id, HolderGetter<Block> blockGetter) {
        var matcher = NamePattern.matcher(templateName(id));
        if (!matcher.matches()) {
            return Optional.empty();
        }

        var sizeX = parseDimension(matcher.group(1));
        var sizeY = parseDimension(matcher.group(2));
        var sizeZ = parseDimension(matcher.group(3));
        if (sizeX.isEmpty() || sizeY.isEmpty() || sizeZ.isEmpty()) {
            return Optional.empty();
        }

        var template = new StructureTemplate();
        template.load(blockGetter, templateTag(sizeX.get(), sizeY.get(), sizeZ.get()));
        return Optional.of(template);
    }

    private static String templateName(ResourceLocation id) {
        var path = id.getPath();
        var slash = path.lastIndexOf('/');
        var segment = slash >= 0 ? path.substring(slash + 1) : path;
        var dot = segment.lastIndexOf('.');
        return dot >= 0 ? segment.substring(dot + 1) : segment;
    }

    private static Optional<Integer> parseDimension(String value) {
        try {
            var dimension = Integer.parseInt(value);
            return dimension >= 1 && dimension <= MaxDimension ? Optional.of(dimension) : Optional.empty();
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private static CompoundTag templateTag(int sizeX, int sizeY, int sizeZ) {
        var tag = new CompoundTag();
        tag.put(StructureTemplate.SIZE_TAG, intList(sizeX, sizeY, sizeZ));
        tag.put(StructureTemplate.PALETTE_TAG, palette());
        tag.put(StructureTemplate.BLOCKS_TAG, new ListTag());
        tag.put(StructureTemplate.ENTITIES_TAG, new ListTag());
        return tag;
    }

    private static ListTag palette() {
        var palette = new ListTag();
        var air = new CompoundTag();
        air.putString("Name", "minecraft:air");
        palette.add(air);
        return palette;
    }

    private static ListTag intList(int... values) {
        var ret = new ListTag();
        for (var value : values) {
            ret.add(IntTag.valueOf(value));
        }
        return ret;
    }
}
