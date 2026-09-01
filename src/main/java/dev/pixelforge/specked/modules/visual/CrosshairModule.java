package dev.pixelforge.specked.modules.visual;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModule extends Module {

    public enum CrosshairStyle { PLUS, DOT, CROSS, ARROW, CIRCLE }

    public CrosshairModule() {
        super("Custom Crosshair", "Replaces the default crosshair", Category.VISUAL);
        settings.add(new Setting<>("Style", "PLUS"));       // 0
        settings.add(new Setting<>("Size", 5.0, 1.0, 20.0));  // 1
        settings.add(new Setting<>("Thickness", 1.0, 1.0, 4.0)); // 2
        settings.add(new Setting<>("Color R", 255.0, 0.0, 255.0)); // 3
        settings.add(new Setting<>("Color G", 255.0, 0.0, 255.0)); // 4
        settings.add(new Setting<>("Color B", 255.0, 0.0, 255.0)); // 5
        settings.add(new Setting<>("Alpha", 255.0, 50.0, 255.0));  // 6
        settings.add(new Setting<>("Gap", 2.0, 0.0, 10.0));        // 7
    }

    public void render(DrawContext ctx) {
        if (mc.options.getPerspective().isFirstPerson() == false) return;
        if (mc.currentScreen != null) return;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int cx = sw / 2;
        int cy = sh / 2;

        int size = ((Double) settings.get(1).getValue()).intValue();
        int thickness = ((Double) settings.get(2).getValue()).intValue();
        int r = ((Double) settings.get(3).getValue()).intValue();
        int g = ((Double) settings.get(4).getValue()).intValue();
        int b = ((Double) settings.get(5).getValue()).intValue();
        int a = ((Double) settings.get(6).getValue()).intValue();
        int gap = ((Double) settings.get(7).getValue()).intValue();
        int color = (a << 24) | (r << 16) | (g << 8) | b;

        String style = (String) settings.get(0).getValue();

        switch (style) {
            case "PLUS" -> {
                // Horizontal
                ctx.fill(cx - size - gap, cy - thickness / 2, cx - gap, cy + thickness / 2 + 1, color);
                ctx.fill(cx + gap, cy - thickness / 2, cx + size + gap, cy + thickness / 2 + 1, color);
                // Vertical
                ctx.fill(cx - thickness / 2, cy - size - gap, cx + thickness / 2 + 1, cy - gap, color);
                ctx.fill(cx - thickness / 2, cy + gap, cx + thickness / 2 + 1, cy + size + gap, color);
            }
            case "DOT" -> ctx.fill(cx - size / 2, cy - size / 2, cx + size / 2 + 1, cy + size / 2 + 1, color);
            case "CROSS" -> {
                ctx.fill(cx - size, cy - thickness / 2, cx + size, cy + thickness / 2 + 1, color);
                ctx.fill(cx - thickness / 2, cy - size, cx + thickness / 2 + 1, cy + size, color);
            }
            case "ARROW" -> {
                ctx.fill(cx - size, cy, cx, cy + size, color);
                ctx.fill(cx, cy, cx + size, cy + size, color);
                ctx.fill(cx - thickness / 2, cy - size, cx + thickness / 2 + 1, cy, color);
            }
            case "CIRCLE" -> {
                for (int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i);
                    int px = (int)(cx + size * Math.cos(rad));
                    int py = (int)(cy + size * Math.sin(rad));
                    ctx.fill(px, py, px + thickness, py + thickness, color);
                }
            }
            default -> {
                ctx.fill(cx - size - gap, cy - thickness / 2, cx - gap, cy + thickness / 2 + 1, color);
                ctx.fill(cx + gap, cy - thickness / 2, cx + size + gap, cy + thickness / 2 + 1, color);
                ctx.fill(cx - thickness / 2, cy - size - gap, cx + thickness / 2 + 1, cy - gap, color);
                ctx.fill(cx - thickness / 2, cy + gap, cx + thickness / 2 + 1, cy + size + gap, color);
            }
        }
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        render(ctx);
    }
}
