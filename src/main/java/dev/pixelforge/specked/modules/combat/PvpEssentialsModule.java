package dev.pixelforge.specked.modules.combat;

import dev.pixelforge.specked.modules.Module;

public class PvpEssentialsModule extends Module {
    public PvpEssentialsModule() {
        super("PvP Essentials", "Shows hit direction, attack cooldown indicator", Category.COMBAT);
        settings.add(new Setting<>("Attack Indicator", true));
        settings.add(new Setting<>("Hit Direction", true));
    }
}
