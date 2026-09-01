package dev.pixelforge.specked.mixin;

import dev.pixelforge.specked.modules.visual.ZoomModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyVariable(method = "getFov", at = @At("RETURN"), ordinal = 0)
    private double modifyFov(double fov) {
        return ZoomModule.getTargetFov((float) fov);
    }
}
