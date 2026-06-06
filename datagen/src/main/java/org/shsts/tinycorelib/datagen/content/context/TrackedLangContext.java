package org.shsts.tinycorelib.datagen.content.context;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedType;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TrackedLangContext extends TrackedContext<String> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Set<String> locales = new HashSet<>();
    private final Multimap<String, String> processed = ArrayListMultimap.create();

    public TrackedLangContext(Registrate registrate) {
        super(registrate, TrackedType.LANG);
    }

    public void addLocale(String locale) {
        locales.add(locale);
    }

    public void process(String locale, String key) {
        processed.put(locale, key);
    }

    @Override
    public boolean postValidate() {
        var tracked = getTracked();
        var ret = true;
        for (var locale : locales) {
            var missing = 0;
            for (var entry : tracked) {
                if (!processed.containsEntry(locale, entry)) {
                    LOGGER.trace("Language {} {} not processed", locale, entry);
                    missing++;
                }
            }
            if (missing > 0) {
                LOGGER.warn("Language {} has {} / {} objects not processed",
                    locale, missing, tracked.size());
                ret = false;
            } else {
                LOGGER.info("Language {} all processed", locale);
            }
        }
        return ret;
    }
}
