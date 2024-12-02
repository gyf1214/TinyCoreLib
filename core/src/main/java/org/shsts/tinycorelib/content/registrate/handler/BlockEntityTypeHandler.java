package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.shsts.tinycorelib.content.registrate.BlockEntityTypeEntry;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.BlockEntityTypeBuilder;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityTypeHandler extends EntryHandler<BlockEntityType<?>> {
    public BlockEntityTypeHandler(Registrate registrate) {
        super(registrate, ForgeRegistries.BLOCK_ENTITIES);
    }

    public BlockEntityTypeEntry getTypeEntry(ResourceLocation loc) {
        return new BlockEntityTypeEntry(loc, () -> RegistryObject.create(loc, getRegistry()).get());
    }

    public BlockEntityTypeEntry getTypeEntry(String id) {
        return getTypeEntry(new ResourceLocation(modid, id));
    }

    public BlockEntityTypeEntry registerType(BlockEntityTypeBuilder<?> builder) {
        builders.add(builder);
        return new BlockEntityTypeEntry(builder.loc());
    }
}
