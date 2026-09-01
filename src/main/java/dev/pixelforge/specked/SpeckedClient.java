package dev.pixelforge.specked;

import dev.pixelforge.specked.config.SpeckedConfig;
import dev.pixelforge.specked.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SpeckedClient implements ClientModInitializer {

    public static final String MOD_ID = "specked";
    public static ModuleManager moduleManager;
    public static SpeckedConfig config;
    public static KeyBinding openGuiKey;

    public static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(
        Identifier.of(MOD_ID, "category")
    );

    @Override
    public void onInitializeClient() {
        config = SpeckedConfig.load();
        moduleManager = new ModuleManager();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.specked.opengui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new dev.pixelforge.specked.gui.SpeckedScreen());
                }
            }
            if (moduleManager != null) moduleManager.onTick(client);
        });
    }
}
