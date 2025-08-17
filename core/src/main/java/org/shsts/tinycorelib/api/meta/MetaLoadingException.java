package org.shsts.tinycorelib.api.meta;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MetaLoadingException extends RuntimeException {
    public MetaLoadingException(String message) {
        super(message);
    }

    public MetaLoadingException(Throwable cause) {
        super(cause);
    }

    public MetaLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
