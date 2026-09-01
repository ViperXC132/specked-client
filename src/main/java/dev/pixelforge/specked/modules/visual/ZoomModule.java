package dev.pixelforge.specked.modules.visual;

import dev.pixelforge.specked.modules.Module;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomModule extends Module {

    public static KeyBinding zoomKey;
    public static float zoomFov = 30f;
    public static float smoothFov = -1f;

    public ZoomModule() {
        super("Zoom", "OptiFine-style zoom with smooth transition", Category.VISUAL);
        settings.add(new Setting<>("Zoom FOV", 30.0, 5.0, 60.0));
        settings.add(new Setting<>("Smooth Zoom", true));
        settings.add(new Setting<>("Scroll to Zoom", true));

        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.specked.zoom",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.specked"
        ));
    }

    public static boolean isZooming() {
        return zoomKey != null && zoomKey.isPressed();
    }

    public static float getTargetFov(float original) {
        if (!isZooming()) {
            smoothFov = original;
            return original;
        }
        float target = zoomFov;
        if (smoothFov < 0) smoothFov = original;
        smoothFov += (target - smoothFov) * 0.15f;
        return smoothFov;
    }
}
