package org.shsts.tinycorelib.datagen.content.context;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.util.Lazy;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedType;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TrackedContext<V> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final TrackedType<V> type;
    private final Supplier<Map<V, String>> tracked;
    private final Map<V, String> extraTracked = new HashMap<>();
    private final List<Supplier<? extends V>> processed;

    public TrackedContext(Registrate registrate, TrackedType<V> type) {
        this.type = type;
        this.tracked = Lazy.of(() -> registrate.getTracked(type));
        this.processed = new ArrayList<>();
    }

    public Map<V, String> getTrackedMap() {
        var ret = new HashMap<>(tracked.get());
        ret.putAll(extraTracked);
        return ret;
    }

    public Set<V> getTracked() {
        return getTrackedMap().keySet();
    }

    public void process(V obj) {
        processed.add(() -> obj);
    }

    public void process(Supplier<? extends V> obj) {
        processed.add(obj);
    }

    public void trackExtra(V obj, String key) {
        extraTracked.put(obj, key);
    }

    public void postValidate() {
        var processed = this.processed.stream().map($ -> (V) $.get())
            .collect(Collectors.toSet());
        var tracked = getTrackedMap();

        var missing = 0;
        for (var entry : tracked.entrySet()) {
            if (!processed.contains(entry.getKey())) {
                LOGGER.trace("{} {} not processed", type, entry.getValue());
                missing++;
            }
        }
        if (missing > 0) {
            LOGGER.warn("{} has {} / {} objects not processed",
                type, missing, tracked.size());
        } else {
            LOGGER.info("{} all processed", type);
        }
    }
}
