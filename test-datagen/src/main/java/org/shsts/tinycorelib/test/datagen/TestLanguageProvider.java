package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.shsts.tinycorelib.datagen.api.IDataGen;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestLanguageProvider extends LanguageProvider {
    private final String locale;
    private final IDataGen dataGen;

    public TestLanguageProvider(IDataGen dataGen, GatherDataEvent event, String locale) {
        super(event.getGenerator(), dataGen.modid(), locale);
        this.dataGen = dataGen;
        dataGen.trackLocale(locale);
        this.locale = locale;
    }

    public TestLanguageProvider(IDataGen dataGen, GatherDataEvent event) {
        this(dataGen, event, "en_us");
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        dataGen.processLang(locale, key);
    }

    @Override
    protected void addTranslations() {
        add("tinycorelib_test.test_resource.test1", "Test 1 Resource");
        add("tinycorelib_test.test_resource.test2", "Test 2 Resource");
        add("block.tinycorelib_test.test_block1", "Test Block 1");
        add("block.tinycorelib_test.test_block3", "Test Block 3");
    }
}
