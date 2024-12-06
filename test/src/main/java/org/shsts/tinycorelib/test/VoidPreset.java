package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.common.world.ForgeWorldPreset;

import java.util.Optional;

import static org.shsts.tinycorelib.test.All.VOID_BIOME;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidPreset extends ForgeWorldPreset {
    public VoidPreset() {
        super((registryAccess, seed) -> {
            var biomes = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
            var structureSets = registryAccess.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
            FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(Optional.empty(), biomes);
            settings.setBiome(biomes.getOrCreateHolder(VOID_BIOME));
            settings.updateLayers();
            return new FlatLevelSource(structureSets, settings);
        });
    }
}
