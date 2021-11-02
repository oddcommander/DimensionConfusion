package com.outlook.oddcommander.dic.mixin;

import net.minecraft.world.dimension.AreaHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaHelper.class)
public abstract class AreaHelperMixin {
    @Inject(at = @At("HEAD"),method = "createPortal",cancellable = true)
    private void banNetherPortal(CallbackInfo ci){
        ci.cancel();
    }
}
