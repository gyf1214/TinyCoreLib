package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IOnUseArg {
    Player player();

    InteractionHand hand();

    BlockHitResult hitResult();
}
