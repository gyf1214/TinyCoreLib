package org.shsts.tinycorelib.test.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;
import org.shsts.tinycorelib.datagen.api.IDataHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestResourceProvider implements DataProvider {
    public static final ExistingFileHelper.ResourceType RESOURCE_TYPE =
        new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", "test_resources");

    private final String modid;
    private final IDataHandler<TestResourceProvider> handler;
    private final DataGenerator generator;
    private final ExistingFileHelper existingFileHelper;
    private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
    private final List<TestResourceBuilder<?>> builders = new ArrayList<>();

    public TestResourceProvider(IDataGen dataGen,
        IDataHandler<TestResourceProvider> handler, GatherDataEvent event) {
        this.modid = dataGen.modid();
        this.handler = handler;
        this.generator = event.getGenerator();
        this.existingFileHelper = event.getExistingFileHelper();
    }

    public void addBuilder(TestResourceBuilder<?> builder) {
        existingFileHelper.trackGenerated(builder.loc(), RESOURCE_TYPE);
        builders.add(builder);
    }

    private Path getPath(ResourceLocation loc) {
        return generator.getOutputFolder()
            .resolve("data/" + loc.getNamespace() + "/test_resources/" + loc.getPath() + ".json");
    }

    @Override
    public void run(HashCache cache) throws IOException {
        handler.register(this);
        for (var builder : builders) {
            builder.validate(existingFileHelper);
            var jo = builder.buildObject();
            var path = getPath(builder.loc());
            var s = gson.toJson(jo);
            var hash = SHA1.hashUnencodedChars(s).toString();

            if (!Files.exists(path) || !Objects.equals(cache.getHash(path), hash)) {
                Files.createDirectories(path.getParent());
                try (var bw = Files.newBufferedWriter(path)) {
                    bw.write(s);
                }
            }
            cache.putNew(path, hash);
        }
    }

    @Override
    public String getName() {
        return "Test Resources: " + modid;
    }
}
