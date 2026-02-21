package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;

import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestLanguageProvider extends LanguageProvider {
    private final IDataGen dataGen;
    private final Set<String> processed = new HashSet<>();

    public TestLanguageProvider(IDataGen dataGen, GatherDataEvent event) {
        super(event.getGenerator(), dataGen.modid(), "en_us");
        this.dataGen = dataGen;
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        dataGen.processLang(key);
        processed.add(key);
    }

    private void addMissingTr() {
        for (var key : dataGen.getTrackedLang()) {
            if (!processed.contains(key)) {
                super.add(key, "Missing Translation");
            }
        }
    }

    @Override
    protected void addTranslations() {
        add("tinycorelib_test.test_resource.test1", "Test 1 Resource");
        add("tinycorelib_test.test_resource.test2", "Test 2 Resource");
        add("block.tinycorelib_test.test_block1", "Test Block 1");
        add("block.tinycorelib_test.test_block3", "Test Block 3");
        addMissingTr();
    }
}
