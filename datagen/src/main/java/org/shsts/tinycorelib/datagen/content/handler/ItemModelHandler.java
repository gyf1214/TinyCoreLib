package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.context.IEntryDataContext;
import org.shsts.tinycorelib.datagen.content.DataGen;
import org.shsts.tinycorelib.datagen.content.context.DataContext;
import org.shsts.tinycorelib.datagen.content.context.EntryDataContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemModelHandler extends DataHandler<ItemModelProvider> {
    public ItemModelHandler(DataGen dataGen) {
        super(dataGen);
    }

    private class Provider extends ItemModelProvider {
        public Provider(GatherDataEvent event) {
            super(event.getGenerator(), dataGen.modid, event.getExistingFileHelper());
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
        protected void registerModels() {
            ItemModelHandler.this.register(this);
        }
    }

    @Override
    protected ItemModelProvider createProvider(GatherDataEvent event) {
        return new Provider(event);
    }

    public <U extends Item> void addModelCallback(ResourceLocation loc, Supplier<U> item,
        Consumer<IEntryDataContext<Item, U, ItemModelProvider>> cons) {
        addCallback(prov -> cons.accept(new EntryDataContext<>(
            dataGen.modid, loc.getPath(), prov, item.get())));
    }

    public <U extends Block> void addBlockItemCallback(ResourceLocation loc, Supplier<U> block,
        Consumer<IEntryDataContext<Item, ? super BlockItem, ItemModelProvider>> cons) {
        addCallback(prov -> {
            if (block.get().asItem() instanceof BlockItem blockItem) {
                cons.accept(new EntryDataContext<>(dataGen.modid,
                    loc.getPath(), prov, blockItem));
            }
        });
    }

    public void addModelCallback(Consumer<DataContext<ItemModelProvider>> cons) {
        addCallback(prov -> cons.accept(new DataContext<>(dataGen.modid, prov)));
    }
}
