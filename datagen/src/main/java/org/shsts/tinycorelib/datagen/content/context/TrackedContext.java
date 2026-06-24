package org.shsts.tinycorelib.datagen.content.context;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.tracking.TrackedType;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TrackedContext<V> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Registrate registrate;
    private final TrackedType<V> type;
    private final Map<V, String> extraTracked = new HashMap<>();
    private final List<V> processed;

    public TrackedContext(Registrate registrate, TrackedType<V> type) {
        this.registrate = registrate;
        this.type = type;
        this.processed = new ArrayList<>();
    }

    public Map<V, String> getTrackedMap() {
        var ret = new HashMap<>(registrate.getTracked(type));
        ret.putAll(extraTracked);
        return ret;
    }

    public Set<V> getTracked() {
        return getTrackedMap().keySet();
    }

    public void process(V obj) {
        processed.add(obj);
    }

    public void trackExtra(V obj, String key) {
        extraTracked.put(obj, key);
    }

    public boolean postValidate() {
        var processed = this.processed.stream().collect(Collectors.toSet());
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
            return false;
        } else {
            LOGGER.info("{} all processed", type);
            return true;
        }
    }
}
