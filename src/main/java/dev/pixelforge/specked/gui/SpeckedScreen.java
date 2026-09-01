package dev.pixelforge.specked.gui;

import dev.pixelforge.specked.SpeckedClient;
import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class SpeckedScreen extends Screen {
    private static final int LEFT_W = 110, MID_W = 160, RIGHT_W = 180;
    private static final int HEADER_H = 28, ROW_H = 22, PADDING = 6;
    private int accentColor = 0xFF4FC3F7, panelAlpha = 0xB0;
    private int textPrimary = 0xFFFFFFFF, textMuted = 0xFFAAAAAA;
    private Module.Category selectedCategory = Module.Category.HUD;
    private Module selectedModule;
    private int scrollOffset;

    public SpeckedScreen() { super(Text.literal("Specked Client")); }
    @Override public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);
        int totalW = LEFT_W + MID_W + RIGHT_W;
        int totalH = Math.min(height - 40, 320);
        int ox = (width - totalW) / 2, oy = (height - totalH) / 2;
        drawLeft(ctx, ox, oy, totalH, mouseX, mouseY);
        drawMiddle(ctx, ox + LEFT_W, oy, totalH, mouseX, mouseY);
        drawRight(ctx, ox + LEFT_W + MID_W, oy, totalH);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawLeft(DrawContext ctx, int x, int y, int h, int mx, int my) {
        ctx.fill(x, y, x + LEFT_W, y + h, (panelAlpha << 24) | 0x111111);
        ctx.fill(x, y, x + LEFT_W, y + HEADER_H, (panelAlpha << 24) | 0x0A0A0A);
        ctx.drawCenteredTextWithShadow(textRenderer, "§b§lSPECKED", x + LEFT_W / 2, y + 9, accentColor);
        int rowY = y + HEADER_H + 4;
        for (Module.Category cat : Module.Category.values()) {
            boolean sel = cat == selectedCategory;
            boolean hov = mx >= x && mx < x + LEFT_W && my >= rowY && my < rowY + ROW_H;
            ctx.fill(x, rowY, x + LEFT_W, rowY + ROW_H, sel ? ((panelAlpha << 24) | 0x1E2A33) : hov ? 0x22FFFFFF : 0);
            if (sel) ctx.fill(x, rowY, x + 3, rowY + ROW_H, accentColor);
            ctx.drawTextWithShadow(textRenderer, catIcon(cat) + " " + cat.name(), x + PADDING + (sel ? 3 : 0), rowY + 7, sel ? accentColor : (hov ? textPrimary : textMuted));
            rowY += ROW_H;
        }
        ctx.drawTextWithShadow(textRenderer, "§7v1.0.0", x + PADDING, y + h - 12, textMuted);
    }

    private void drawMiddle(DrawContext ctx, int x, int y, int h, int mx, int my) {
        ctx.fill(x, y, x + MID_W, y + h, (panelAlpha << 24) | 0x161616);
        ctx.fill(x, y, x + MID_W, y + HEADER_H, (panelAlpha << 24) | 0x0D0D0D);
        ctx.drawTextWithShadow(textRenderer, selectedCategory.name(), x + PADDING, y + 9, textPrimary);
        List<Module> mods = SpeckedClient.moduleManager.getByCategory(selectedCategory);
        int rowY = y + HEADER_H + 4 - scrollOffset;
        for (Module m : mods) {
            if (rowY + ROW_H < y + HEADER_H || rowY > y + h) { rowY += ROW_H; continue; }
            boolean sel = m == selectedModule;
            boolean hov = mx >= x && mx < x + MID_W && my >= rowY && my < rowY + ROW_H;
            ctx.fill(x, rowY, x + MID_W, rowY + ROW_H, sel ? ((panelAlpha << 24) | 0x1A2830) : hov ? 0x18FFFFFF : 0);
            drawPill(ctx, x + MID_W - 32, rowY + 6, m.isEnabled());
            ctx.drawTextWithShadow(textRenderer, m.getName(), x + PADDING, rowY + 7, m.isEnabled() ? textPrimary : textMuted);
            rowY += ROW_H;
        }
    }

    private void drawRight(DrawContext ctx, int x, int y, int h) {
        ctx.fill(x, y, x + RIGHT_W, y + h, (panelAlpha << 24) | 0x111116);
        ctx.fill(x, y, x + RIGHT_W, y + HEADER_H, (panelAlpha << 24) | 0x0A0A12);
        ctx.drawTextWithShadow(textRenderer, selectedModule == null ? "Settings" : selectedModule.getName(), x + PADDING, y + 9, accentColor);
        if (selectedModule == null) {
            ctx.drawCenteredTextWithShadow(textRenderer, "§7Select a module", x + RIGHT_W / 2, y + h / 2 - 4, textMuted);
            return;
        }
        int rowY = y + HEADER_H + 6;
        for (Module.Setting<?> setting : selectedModule.getSettings()) {
            ctx.drawTextWithShadow(textRenderer, "§7" + setting.getName(), x + PADDING, rowY, textMuted);
            rowY += 10;
            Object val = setting.getValue();
            if (val instanceof Boolean b) { drawPill(ctx, x + PADDING, rowY, b); rowY += 16; }
            else if (val instanceof Number n && setting.isSlider()) {
                double d = n.doubleValue(), mn = ((Number) setting.getMin()).doubleValue(), mx = ((Number) setting.getMax()).doubleValue();
                int trackW = RIGHT_W - PADDING * 2;
                float pct = (float)((d - mn) / (mx - mn));
                ctx.fill(x + PADDING, rowY, x + PADDING + trackW, rowY + 6, 0xFF1A1A1A);
                ctx.fill(x + PADDING, rowY, x + PADDING + (int)(trackW * pct), rowY + 6, accentColor);
                ctx.drawTextWithShadow(textRenderer, String.format("%.1f", d), x + PADDING + trackW - 20, rowY - 2, textPrimary);
                rowY += 14;
            } else if (val instanceof String s) {
                ctx.fill(x + PADDING, rowY, x + PADDING + textRenderer.getWidth(s) + 6, rowY + 12, 0x44FFFFFF);
                ctx.drawTextWithShadow(textRenderer, s, x + PADDING + 3, rowY + 2, textPrimary);
                rowY += 16;
            }
            rowY += 4;
        }
        int sliderY = y + h - 30;
        ctx.drawTextWithShadow(textRenderer, "§7Panel Opacity", x + PADDING, y + h - 40, textMuted);
        int trackW = RIGHT_W - PADDING * 2;
        ctx.fill(x + PADDING, sliderY, x + PADDING + trackW, sliderY + 6, 0xFF1A1A1A);
        ctx.fill(x + PADDING, sliderY, x + PADDING + (int)(trackW * panelAlpha / 255f), sliderY + 6, accentColor);
        ctx.drawTextWithShadow(textRenderer, panelAlpha + "/255", x + PADDING + trackW - 30, sliderY - 2, textPrimary);
    }

    private void drawPill(DrawContext ctx, int x, int y, boolean on) {
        int track = on ? accentColor : 0xFF333333;
        ctx.fill(x, y, x + 24, y + 10, track);
        ctx.fill(x + 1, y + 1, x + 23, y + 9, on ? ((accentColor & 0x00FFFFFF) | 0x88000000) : 0x88000000);
        int knob = on ? x + 14 : x + 2;
        ctx.fill(knob, y + 1, knob + 8, y + 9, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int totalW = LEFT_W + MID_W + RIGHT_W, totalH = Math.min(height - 40, 320);
        int ox = (width - totalW) / 2, oy = (height - totalH) / 2;
        int mx = (int) mouseX, my = (int) mouseY;
        if (mx >= ox && mx < ox + LEFT_W) {
            int rowY = oy + HEADER_H + 4;
            for (Module.Category cat : Module.Category.values()) {
                if (my >= rowY && my < rowY + ROW_H) { selectedCategory = cat; selectedModule = null; scrollOffset = 0; return true; }
                rowY += ROW_H;
            }
        }
        if (mx >= ox + LEFT_W && mx < ox + LEFT_W + MID_W) {
            List<Module> mods = SpeckedClient.moduleManager.getByCategory(selectedCategory);
            int rowY = oy + HEADER_H + 4 - scrollOffset;
            for (Module m : mods) {
                if (my >= rowY && my < rowY + ROW_H) {
                    int pillX = ox + LEFT_W + MID_W - 32;
                    if (mx >= pillX && mx < pillX + 24) m.toggle();
                    else selectedModule = selectedModule == m ? null : m;
                    return true;
                }
                rowY += ROW_H;
            }
        }
        if (selectedModule != null && mx >= ox + LEFT_W + MID_W && mx < ox + totalW) {
            int rx = ox + LEFT_W + MID_W, rowY = oy + HEADER_H + 6;
            for (Module.Setting<?> setting : selectedModule.getSettings()) {
                rowY += 10;
                Object val = setting.getValue();
                if (val instanceof Boolean) {
                    if (my >= rowY && my < rowY + 16) {
                        @SuppressWarnings("unchecked") Module.Setting<Boolean> s = (Module.Setting<Boolean>) setting;
                        s.setValue(!s.getValue()); return true;
                    }
                    rowY += 16;
                } else if (val instanceof Number && setting.isSlider()) {
                    if (my >= rowY && my < rowY + 6) {
                        int trackW = RIGHT_W - PADDING * 2;
                        float pct = Math.max(0, Math.min(1, (float)(mx - rx - PADDING) / trackW));
                        @SuppressWarnings("unchecked") Module.Setting<Object> s = (Module.Setting<Object>) setting;
                        if (val instanceof Double) s.setValue(((Number) setting.getMin()).doubleValue() + (((Number) setting.getMax()).doubleValue() - ((Number) setting.getMin()).doubleValue()) * pct);
                        return true;
                    }
                    rowY += 14;
                } else if (val instanceof String) rowY += 16;
                rowY += 4;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        mouseClicked(click, false);
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int totalW = LEFT_W + MID_W + RIGHT_W, ox = (width - totalW) / 2;
        if (mouseX >= ox + LEFT_W && mouseX < ox + LEFT_W + MID_W) scrollOffset = Math.max(0, scrollOffset - (int)(verticalAmount * ROW_H));
        return true;
    }

    private String catIcon(Module.Category cat) {
        return switch (cat) { case COMBAT -> "⚔"; case HUD -> "◈"; case MOVEMENT -> "↑"; case VISUAL -> "◉"; case UTILITY -> "⚙"; };
    }
}
