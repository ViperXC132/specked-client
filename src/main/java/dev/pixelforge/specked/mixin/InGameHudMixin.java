package dev.pixelforge.specked.mixin;

import dev.pixelforge.specked.SpeckedClient;
import dev.pixelforge.specked.modules.visual.CrosshairModule;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultCrosshair(DrawContext ctx, float tickDelta, CallbackInfo ci) {
        if (SpeckedClient.moduleManager == null) return;
        var mods = SpeckedClient.moduleManager.getAll();
        for (var m : mods) {
            if (m instanceof CrosshairModule && m.isEnabled()) {
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext ctx, float tickDelta, CallbackInfo ci) {
        if (SpeckedClient.moduleManager != null) {
            SpeckedClient.moduleManager.onHudRender(ctx, tickDelta);
        }
    }
}
