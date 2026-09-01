package dev.pixelforge.specked.modules.hud;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

public class KeystrokesModule extends Module {

    public KeystrokesModule() {
        super("Keystrokes", "Shows WASD and mouse click indicators", Category.HUD);
        settings.add(new Setting<>("X Position", 4.0, 0.0, 500.0));
        settings.add(new Setting<>("Y Position", 220.0, 0.0, 500.0));
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (mc.player == null || mc.options == null) return;
        int x = ((Double) settings.get(0).getValue()).intValue();
        int y = ((Double) settings.get(1).getValue()).intValue();

        GameOptions opts = mc.options;
        drawKey(ctx, "W", x + 14, y, opts.forwardKey.isPressed());
        drawKey(ctx, "A", x, y + 14, opts.leftKey.isPressed());
        drawKey(ctx, "S", x + 14, y + 14, opts.backKey.isPressed());
        drawKey(ctx, "D", x + 28, y + 14, opts.rightKey.isPressed());
        drawKey(ctx, "LMB", x, y + 28, opts.attackKey.isPressed());
        drawKey(ctx, "RMB", x + 22, y + 28, opts.useKey.isPressed());
    }

    private void drawKey(DrawContext ctx, String label, int x, int y, boolean pressed) {
        int bg = pressed ? 0xCC4FC3F7 : 0x88000000;
        int textColor = pressed ? 0xFF000000 : 0xFFFFFFFF;
        int w = Math.max(12, mc.textRenderer.getWidth(label) + 4);
        ctx.fill(x, y, x + w, y + 12, bg);
        ctx.drawBorder(x, y, w, 12, 0x44FFFFFF);
        ctx.drawCenteredString(mc.textRenderer, label, x + w / 2, y + 2, textColor);
    }
}
