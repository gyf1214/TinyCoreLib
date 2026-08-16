package org.shsts.tinycorelib.content.meta;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.shsts.tinycorelib.api.meta.MetaLoadingException;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MetaLocator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ROOT_FOLDER = "meta";
    private static final String SUFFIX = ".json";
    private static final String DELETE_KEY = "tinycorelib:delete";

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private final Gson gson = new Gson();

    private record MetaFile(String folder, ResourceLocation loc, Path path) {}

    private enum MetaOperation {
        CONTENT,
        DELETE,
        INVALID
    }

    private final Map<String, Map<ResourceLocation, MetaContent>> allMeta = new HashMap<>();

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
                        var path3 = path2.substring(0, path2.length() - SUFFIX.length());
                        try {
                            var loc = ResourceLocation.fromNamespaceAndPath(namespace, path3);
                            allFiles.add(new MetaFile(folder, loc, path));
                        } catch (ResourceLocationException e) {
                            LOGGER.error("invalid meta file path {}, skip", path, e);
                        }
                    });
            }
        }

        LOGGER.debug("process {} meta files", allFiles.size());

        for (var file : allFiles) {
            try (var is = Files.newInputStream(file.path);
                var reader = new InputStreamReader(is)) {
                var jo = gson.fromJson(reader, JsonObject.class);
                if (jo == null) {
                    LOGGER.error("meta file {} does not contain an object, skip", file.path);
                    continue;
                }

                var operation = getOperation(file, jo);
                if (operation == MetaOperation.CONTENT) {
                    putContent(file, jo);
                } else if (operation == MetaOperation.DELETE) {
                    deleteContent(file);
                }
            } catch (IOException | JsonParseException e) {
                LOGGER.error("unable to parse meta file {}, skip", file.path, e);
            }
        }

        LOGGER.debug("finish processing meta, total folders={}, total meta={}",
            allMeta.size(), allMeta.values().stream().mapToInt(Map::size).sum());
    }

    private MetaOperation getOperation(MetaFile file, JsonObject jo) {
        var delete = jo.get(DELETE_KEY);
        if (delete == null) {
            return MetaOperation.CONTENT;
        }
        if (jo.size() != 1 || !delete.isJsonPrimitive() ||
            !delete.getAsJsonPrimitive().isBoolean() || !delete.getAsBoolean()) {
            LOGGER.error("invalid delete marker in meta file {}, skip", file.path);
            return MetaOperation.INVALID;
        }
        return MetaOperation.DELETE;
    }

    private void putContent(MetaFile file, JsonObject jo) {
        var metas = allMeta.computeIfAbsent(file.folder, $ -> new LinkedHashMap<>());
        var previous = metas.remove(file.loc);
        metas.put(file.loc, new MetaContent(file.loc, jo));
        var operation = previous == null ? "add" : "replace";
        LOGGER.debug("meta {} key={}/{} source={}", operation, file.folder, file.loc, file.path);
    }

    private void deleteContent(MetaFile file) {
        var metas = allMeta.get(file.folder);
        if (metas == null || metas.remove(file.loc) == null) {
            LOGGER.debug("meta delete missing key={}/{} source={}", file.folder, file.loc,
                file.path);
            return;
        }
        if (metas.isEmpty()) {
            allMeta.remove(file.folder);
        }
        LOGGER.debug("meta delete key={}/{} source={}", file.folder, file.loc, file.path);
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
        var metas = allMeta.get(folder);
        return metas == null ? Collections.emptyList() : metas.values();
    }
}
