package dev.pixelforge.specked.modules.combat;

import dev.pixelforge.specked.modules.Module;

public class ShieldFixesModule extends Module {
    public ShieldFixesModule() {
        super("Shield Fixes", "Fixes shield activation delay and desync", Category.COMBAT);
        settings.add(new Setting<>("Fix Desync", true));
        settings.add(new Setting<>("Instant Block", false));
    }
    // Logic injected via GameRendererMixin — flag checked there
}
