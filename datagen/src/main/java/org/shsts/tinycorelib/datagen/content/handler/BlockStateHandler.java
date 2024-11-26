package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;
import org.shsts.tinycorelib.datagen.content.DataGen;
import org.shsts.tinycorelib.datagen.content.context.DataContext;
import org.shsts.tinycorelib.datagen.content.context.EntryDataContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockStateHandler extends DataHandler<BlockStateProvider> {
    public BlockStateHandler(DataGen dataGen) {
        super(dataGen);
    }

    private static class BlockProvider extends BlockModelProvider {
        public BlockProvider(GatherDataEvent event, String modid) {
            super(event.getGenerator(), modid, event.getExistingFileHelper());
        }

        /**
         * Convention: all withExistingParent should use full path.
         */
        @Override
        public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
            var ret = new ModelFile.ExistingModelFile(path, existingFileHelper);
            ret.assertExistence();
            return ret;
        }

        /**
         * Convention: all getBuilder should automatically include the folder prefix.
         */
        @Override
        public BlockModelBuilder getBuilder(String path) {
            return super.getBuilder(modelPath(path, modid, folder));
        }

        @Override
        protected void registerModels() {}
    }

    private static class ItemProvider extends ItemModelProvider {
        public ItemProvider(GatherDataEvent event, String modid) {
            super(event.getGenerator(), modid, event.getExistingFileHelper());
        }

        /**
         * Convention: all withExistingParent should use full path.
         */
        @Override
        public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
            var ret = new ModelFile.ExistingModelFile(path, existingFileHelper);
            ret.assertExistence();
            return ret;
        }

        /**
         * Convention: all getBuilder should automatically include the folder prefix.
         */
        @Override
        public ItemModelBuilder getBuilder(String path) {
            return super.getBuilder(modelPath(path, modid, folder));
        }

        @Override
        protected void registerModels() {}
    }

    private class Provider extends BlockStateProvider {
        private final ItemModelProvider itemModels;
        private final BlockModelProvider blockModels;

        public Provider(GatherDataEvent event) {
            super(event.getGenerator(), dataGen.modid, event.getExistingFileHelper());
            this.blockModels = new BlockProvider(event, dataGen.modid);
            this.itemModels = new ItemProvider(event, dataGen.modid);
        }

        @Override
        public BlockModelProvider models() {
            return blockModels;
        }

        @Override
        public ItemModelProvider itemModels() {
            return itemModels;
        }

        @Override
        protected void registerStatesAndModels() {
            BlockStateHandler.this.register(this);
        }
    }

    @Override
    protected BlockStateProvider createProvider(GatherDataEvent event) {
        return new Provider(event);
    }

    public <U extends Block> void addBlockStateCallback(ResourceLocation loc, Supplier<U> block,
        Consumer<IEntryDataContext<Block, U, BlockStateProvider>> cons) {
        addCallback(prov -> cons.accept(new EntryDataContext<>(dataGen.modid,
            loc.getPath(), prov, block.get())));
    }

    public void addBlockModelCallback(Consumer<IDataContext<BlockModelProvider>> cons) {
        addCallback(prov -> cons.accept(new DataContext<>(dataGen.modid, prov.models())));
    }
}
