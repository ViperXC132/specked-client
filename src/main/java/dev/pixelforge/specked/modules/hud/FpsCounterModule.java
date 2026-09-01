package dev.pixelforge.specked.modules.hud;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;

public class FpsCounterModule extends Module {

    public FpsCounterModule() {
        super("FPS Counter", "Shows current FPS on HUD", Category.HUD);
        settings.add(new Setting<>("X Position", 4.0, 0.0, 500.0));
        settings.add(new Setting<>("Y Position", 90.0, 0.0, 500.0));
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        int x = ((Double) settings.get(0).getValue()).intValue();
        int y = ((Double) settings.get(1).getValue()).intValue();
        int fps = mc.getCurrentFps();
        int color = fps >= 60 ? 0xFF55FF55 : fps >= 30 ? 0xFFFFFF55 : 0xFFFF5555;
        String text = "FPS: " + fps;
        int w = mc.textRenderer.getWidth(text);
        ctx.fill(x - 2, y - 2, x + w + 2, y + 10, 0x88000000);
        ctx.drawString(mc.textRenderer, text, x, y, color);
    }
}
