package org.shsts.tinycorelib.datagen.content.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.IDataHandler;
import org.shsts.tinycorelib.datagen.content.DataGen;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class DataHandler<D extends DataProvider> implements IDataHandler<D> {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final DataGen dataGen;
    protected final List<Consumer<D>> callbacks = new ArrayList<>();

    public DataHandler(DataGen dataGen) {
        this.dataGen = dataGen;
    }

    @Override
    public IDataGen dataGen() {
        return dataGen;
    }

    @Override
    public void addCallback(Consumer<D> callback) {
        callbacks.add(callback);
    }

    @Override
    public <B, P> B builder(P parent, String id, BuilderFactory<B, D, P> factory) {
        return factory.create(this, parent, new ResourceLocation(dataGen.modid, id));
    }

    @Override
    public <B> B builder(String id, BuilderFactory<B, D, IDataHandler<D>> factory) {
        return builder(this, id, factory);
    }

    @Override
    public void register(D provider) {
        LOGGER.info("DataHandler{{}}: add {} callbacks", provider.getName(), callbacks.size());
        for (var callback : callbacks) {
            callback.accept(provider);
        }
        callbacks.clear();
    }

    public void onGatherData(GatherDataEvent event) {
        var prov = createProvider(event);
        event.getGenerator().addProvider(prov);
    }

    public static String modelPath(String path, String modid, String folder) {
        var loc = path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path);
        var newPath = loc.getPath();
        if (!newPath.startsWith(ModelProvider.BLOCK_FOLDER + "/") &&
            !newPath.startsWith(ModelProvider.ITEM_FOLDER + "/")) {
            newPath = folder + "/" + newPath;
        }
        return (new ResourceLocation(loc.getNamespace(), newPath)).toString();
    }
}
