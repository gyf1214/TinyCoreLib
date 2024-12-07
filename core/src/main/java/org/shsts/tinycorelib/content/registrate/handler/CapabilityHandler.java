package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.CapabilityEntry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CapabilityHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String modid;
    private final List<Class<?>> capabilities = new ArrayList<>();

    public CapabilityHandler(Registrate registrate) {
        this.modid = registrate.modid;
    }

    public void onRegisterEvent(RegisterCapabilitiesEvent event) {
        if (capabilities.isEmpty()) {
            return;
        }
        LOGGER.info("Mod {} register {} capabilities", modid, capabilities.size());
        for (var cap : capabilities) {
            event.register(cap);
        }
        capabilities.clear();
    }

    public <T> CapabilityEntry<T> register(Class<T> clazz, CapabilityToken<T> token) {
        capabilities.add(clazz);
        return new CapabilityEntry<>(modid, token);
    }
}
