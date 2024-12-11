package org.shsts.tinycorelib.test.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.core.ILoc;
import org.shsts.tinycorelib.datagen.api.IDataHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestResourceBuilder<P> implements IBuilder<JsonObject, P, TestResourceBuilder<P>>, ILoc {
    private final P parent;
    private final ResourceLocation loc;
    private final List<Consumer<JsonObject>> onCreateObject = new ArrayList<>();
    private final List<Runnable> onBuild = new ArrayList<>();

    @Nullable
    private String name = null;
    private final List<ResourceLocation> references = new ArrayList<>();

    private TestResourceBuilder(P parent, ResourceLocation loc) {
        this.parent = parent;
        this.loc = loc;
    }

    public TestResourceBuilder<P> name(String value) {
        name = value;
        return self();
    }

    public TestResourceBuilder<P> reference(ResourceLocation ref) {
        references.add(ref);
        return self();
    }

    public void validate(ExistingFileHelper existingFileHelper) {
        for (var ref : references) {
            if (!existingFileHelper.exists(ref, TestResourceProvider.RESOURCE_TYPE)) {
                throw new IllegalStateException("TestResource %s does not exist".formatted(ref));
            }
        }
    }

    private JsonObject createObject() {
        var jo = new JsonObject();
        assert name != null;
        jo.addProperty("name", name);
        var ja = new JsonArray();
        references.forEach($ -> ja.add($.toString()));
        jo.add("references", ja);
        return jo;
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    @Override
    public JsonObject buildObject() {
        var obj = createObject();
        for (var cb : onCreateObject) {
            cb.accept(obj);
        }
        return obj;
    }

    @Override
    public P build() {
        for (var cb : onBuild) {
            cb.run();
        }
        return parent;
    }

    @Override
    public P end() {
        return parent;
    }

    public ResourceLocation register() {
        build();
        return loc;
    }

    @Override
    public TestResourceBuilder<P> onCreateObject(Consumer<JsonObject> cons) {
        onCreateObject.add(cons);
        return self();
    }

    @Override
    public TestResourceBuilder<P> onBuild(Runnable cb) {
        onBuild.add(cb);
        return self();
    }

    public static TestResourceBuilder<IDataHandler<TestResourceProvider>> builder(
        IDataHandler<TestResourceProvider> handler, ResourceLocation loc) {
        var builder = new TestResourceBuilder<>(handler, loc);
        return builder.onBuild(() -> {
            handler.addCallback(p -> p.addBuilder(builder));
            handler.dataGen().trackLang(loc.getNamespace() + ".test_resource." +
                loc.getPath().replace('/', '.'));
        });
    }
}
