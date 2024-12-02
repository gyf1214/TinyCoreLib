package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import org.shsts.tinycorelib.content.registrate.CapabilityEntry;
import org.shsts.tinycorelib.content.registrate.Registrate;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CapabilityHandler {
    private final Registrate registrate;
    private final List<Class<?>> capabilities = new ArrayList<>();

    public CapabilityHandler(Registrate registrate) {
        this.registrate = registrate;
    }

    public void onRegisterEvent(RegisterCapabilitiesEvent event) {
        for (var cap : capabilities) {
            event.register(cap);
        }
        capabilities.clear();
    }

    public <T> CapabilityEntry<T> register(Class<T> clazz, CapabilityToken<T> token) {
        capabilities.add(clazz);
        return new CapabilityEntry<>(registrate.modid, token);
    }
}
