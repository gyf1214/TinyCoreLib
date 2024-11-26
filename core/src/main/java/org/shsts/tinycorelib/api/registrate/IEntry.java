package org.shsts.tinycorelib.api.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.core.ILoc;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IEntry<U> extends ILoc, Supplier<U> {}
