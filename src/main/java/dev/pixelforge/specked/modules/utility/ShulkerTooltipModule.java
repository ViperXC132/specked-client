package dev.pixelforge.specked.modules.utility;

import dev.pixelforge.specked.modules.Module;

public class ShulkerTooltipModule extends Module {
    public ShulkerTooltipModule() {
        super("Shulker Tooltip", "Preview shulker box contents on hover", Category.UTILITY);
        settings.add(new Setting<>("Show Count", true));
        settings.add(new Setting<>("Compact View", false));
    }
    // Logic injected via mixin on ItemTooltipCallback
}
