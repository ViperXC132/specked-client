package dev.pixelforge.specked.mixin;

import dev.pixelforge.specked.SpeckedClient;
import dev.pixelforge.specked.modules.hud.CpsCounterModule;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != 1 || SpeckedClient.moduleManager == null) return;
        for (var m : SpeckedClient.moduleManager.getAll()) {
            if (m instanceof CpsCounterModule cps && m.isEnabled()) {
                if (button == 0) cps.registerLeft();
                if (button == 1) cps.registerRight();
            }
        }
    }
}
