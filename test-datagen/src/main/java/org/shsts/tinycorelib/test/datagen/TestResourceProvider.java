package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.IDataHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestResourceProvider implements DataProvider {
    public static final ExistingFileHelper.ResourceType RESOURCE_TYPE =
        new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", "test_resources");

    private final String modid;
    private final IDataHandler<TestResourceProvider> handler;
    private final PackOutput.PathProvider pathProvider;
    private final ExistingFileHelper existingFileHelper;
    private final List<TestResourceBuilder<?>> builders = new ArrayList<>();

    public TestResourceProvider(IDataGen dataGen,
        IDataHandler<TestResourceProvider> handler, GatherDataEvent event) {
        this.modid = dataGen.modid();
        this.handler = handler;
        this.pathProvider = event.getGenerator().getPackOutput()
            .createPathProvider(PackOutput.Target.DATA_PACK, "test_resources");
        this.existingFileHelper = event.getExistingFileHelper();
    }

    public void addBuilder(TestResourceBuilder<?> builder) {
        existingFileHelper.trackGenerated(builder.loc(), RESOURCE_TYPE);
        builders.add(builder);
    }

    private Path getPath(ResourceLocation loc) {
        return pathProvider.json(loc);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        handler.register(this);
        var futures = builders.stream().map(builder -> {
            builder.validate(existingFileHelper);
            var jo = builder.buildObject();
            return DataProvider.saveStable(output, jo, getPath(builder.loc()));
        }).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName() {
        return "Test Resources: " + modid;
    }
}
