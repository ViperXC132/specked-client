package dev.pixelforge.specked.modules.visual;

import dev.pixelforge.specked.modules.Module;

public class SkinLayers3DModule extends Module {
    public SkinLayers3DModule() {
        super("3D Skin Layers", "Renders skin outer layers in 3D", Category.VISUAL);
        settings.add(new Setting<>("Layer Depth", 0.25, 0.1, 1.0));
        settings.add(new Setting<>("Show on Self", true));
        settings.add(new Setting<>("Show on Others", true));
    }
    // Rendering logic injected via mixins into PlayerEntityRenderer
}
