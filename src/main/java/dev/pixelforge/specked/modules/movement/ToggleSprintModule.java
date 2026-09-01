package dev.pixelforge.specked.modules.movement;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.MinecraftClient;

public class ToggleSprintModule extends Module {

    public ToggleSprintModule() {
        super("Toggle Sprint", "Hold W to always sprint", Category.MOVEMENT);
        settings.add(new Setting<>("Toggle Mode", false));
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        boolean toggle = (boolean) settings.get(0).getValue();
        if (client.options.forwardKey.isPressed()) {
            client.player.setSprinting(true);
        }
    }
}
