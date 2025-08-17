package org.shsts.tinycorelib.content.meta;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.shsts.tinycorelib.api.meta.MetaLoadingException;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MetaLocator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ROOT_FOLDER = "meta";
    private static final String SUFFIX = ".json";

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private final Gson gson = new Gson();

    private record MetaFile(String folder, ResourceLocation loc, Path path) {}

    private final Multimap<String, MetaContent> allMeta = ArrayListMultimap.create();

    private void unsafeScanFiles() throws IOException {
        var mods = ModList.get().getModFiles();
        var roots = new ArrayList<Path>();
        for (var modInfo : mods) {
            var modFile = modInfo.getFile();
            var modRoot = modFile.findResource(ROOT_FOLDER);
            if (Files.isDirectory(modRoot)) {
                roots.add(modRoot);
            }
        }

        var extraRoot = Paths.get(ROOT_FOLDER);
        if (Files.isDirectory(extraRoot)) {
            roots.add(extraRoot);
        }

        LOGGER.debug("scan {} meta folders", roots.size());

        var allFiles = new ArrayList<MetaFile>();
        for (var root : roots) {
            try (var files = Files.walk(root)) {
                files.filter($ -> Files.isRegularFile($) &&
                        $.getFileName().toString().endsWith(SUFFIX) &&
                        root.relativize($).getNameCount() >= 3)
                    .forEach(path -> {
                        var path1 = root.relativize(path);
                        var namespace = path1.getName(0).toString();
                        var folder = path1.getName(1).toString();
                        var path2 = path1.subpath(2, path1.getNameCount()).toString();
                        var loc = new ResourceLocation(namespace, path2);
                        allFiles.add(new MetaFile(folder, loc, path));
                    });
            }
        }

        LOGGER.debug("process {} meta files", allFiles.size());

        for (var file : allFiles) {
            try (var is = Files.newInputStream(file.path);
                var reader = new InputStreamReader(is)) {
                var jo = gson.fromJson(reader, JsonObject.class);
                allMeta.put(file.folder, new MetaContent(file.loc, jo));
            } catch (JsonParseException e) {
                LOGGER.error("unable to parse meta file {}, skip", file.path, e);
            }
        }

        LOGGER.debug("finish processing meta, total folders={}, total meta={}",
            allMeta.keySet().size(), allMeta.size());
    }

    public void scanFiles() {
        try {
            unsafeScanFiles();
        } catch (IOException e) {
            throw new MetaLoadingException(e);
        }
        future.complete(null);
    }

    public void await() {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new MetaLoadingException(e);
        }
    }

    public Collection<MetaContent> getFolder(String folder) {
        return allMeta.get(folder);
    }
}
