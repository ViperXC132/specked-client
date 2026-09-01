package dev.pixelforge.specked.gui;

import dev.pixelforge.specked.SpeckedClient;
import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class SpeckedScreen extends Screen {

    // Layout constants
    private static final int LEFT_W = 110;
    private static final int MID_W = 160;
    private static final int RIGHT_W = 180;
    private static final int HEADER_H = 28;
    private static final int ROW_H = 22;
    private static final int PADDING = 6;

    // Customizable colors (saved in config)
    private int accentColor   = 0xFF4FC3F7;
    private int panelAlpha    = 0xB0;  // 0–255
    private int textPrimary   = 0xFFFFFFFF;
    private int textMuted     = 0xFFAAAAAA;

    private Module.Category selectedCategory = Module.Category.HUD;
    private Module selectedModule = null;
    private int scrollOffset = 0;

    // Dragging for custom crosshair / HUD positions — future use
    private boolean draggingPanel = false;
    private int dragOffX, dragOffY;

    public SpeckedScreen() { super(Text.literal("Specked Client")); }

    @Override
    public boolean shouldPause() { return false; }

    // ──────────────────────────────────────────────────────────────────
    //  RENDER
    // ──────────────────────────────────────────────────────────────────
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dim background
        ctx.fill(0, 0, width, height, 0x88000000);

        int totalW = LEFT_W + MID_W + RIGHT_W;
        int totalH = Math.min(height - 40, 320);
        int originX = (width - totalW) / 2;
        int originY = (height - totalH) / 2;

        drawLeft(ctx, originX, originY, totalH, mouseX, mouseY);
        drawMiddle(ctx, originX + LEFT_W, originY, totalH, mouseX, mouseY);
        drawRight(ctx, originX + LEFT_W + MID_W, originY, totalH, mouseX, mouseY);

        super.render(ctx, mouseX, mouseY, delta);
    }

    // ── LEFT PANEL: logo + categories ─────────────────────────────────
    private void drawLeft(DrawContext ctx, int x, int y, int h, int mx, int my) {
        int bg = (panelAlpha << 24) | 0x111111;
        ctx.fill(x, y, x + LEFT_W, y + h, bg);

        // Logo header
        ctx.fill(x, y, x + LEFT_W, y + HEADER_H, (panelAlpha << 24) | 0x0A0A0A);
        ctx.drawCenteredString(textRenderer, "§b§lSPECKED", x + LEFT_W / 2, y + 9, accentColor);

        int rowY = y + HEADER_H + 4;
        for (Module.Category cat : Module.Category.values()) {
            boolean sel = cat == selectedCategory;
            boolean hov = mx >= x && mx < x + LEFT_W && my >= rowY && my < rowY + ROW_H;
            int rowBg = sel ? ((panelAlpha << 24) | 0x1E2A33) : hov ? 0x22FFFFFF : 0;
            ctx.fill(x, rowY, x + LEFT_W, rowY + ROW_H, rowBg);
            if (sel) ctx.fill(x, rowY, x + 3, rowY + ROW_H, accentColor);
            String label = catIcon(cat) + " " + cat.name();
            ctx.drawString(textRenderer, label, x + PADDING + (sel ? 3 : 0), rowY + 7, sel ? accentColor : (hov ? textPrimary : textMuted));
            rowY += ROW_H;
        }

        // Version footer
        ctx.drawString(textRenderer, "§7v1.0.0", x + PADDING, y + h - 12, textMuted);
    }

    // ── MIDDLE PANEL: module toggles ──────────────────────────────────
    private void drawMiddle(DrawContext ctx, int x, int y, int h, int mx, int my) {
        int bg = (panelAlpha << 24) | 0x161616;
        ctx.fill(x, y, x + MID_W, y + h, bg);

        // Header
        ctx.fill(x, y, x + MID_W, y + HEADER_H, (panelAlpha << 24) | 0x0D0D0D);
        ctx.drawString(textRenderer, selectedCategory.name(), x + PADDING, y + 9, textPrimary);

        List<Module> mods = SpeckedClient.moduleManager.getByCategory(selectedCategory);
        int rowY = y + HEADER_H + 4 - scrollOffset;

        for (Module m : mods) {
            if (rowY + ROW_H < y + HEADER_H || rowY > y + h) { rowY += ROW_H; continue; }
            boolean sel = m == selectedModule;
            boolean hov = mx >= x && mx < x + MID_W && my >= rowY && my < rowY + ROW_H;
            boolean en  = m.isEnabled();

            int rowBg = sel ? ((panelAlpha << 24) | 0x1A2830) : hov ? 0x18FFFFFF : 0;
            ctx.fill(x, rowY, x + MID_W, rowY + ROW_H, rowBg);

            // Toggle pill
            int pillX = x + MID_W - 32;
            int pillY = rowY + 6;
            drawPill(ctx, pillX, pillY, en);

            // Name
            ctx.drawString(textRenderer, m.getName(), x + PADDING, rowY + 7, en ? textPrimary : textMuted);

            rowY += ROW_H;
        }
    }

    // ── RIGHT PANEL: settings ──────────────────────────────────────────
    private void drawRight(DrawContext ctx, int x, int y, int h, int mx, int my) {
        int bg = (panelAlpha << 24) | 0x111116;
        ctx.fill(x, y, x + RIGHT_W, y + h, bg);

        ctx.fill(x, y, x + RIGHT_W, y + HEADER_H, (panelAlpha << 24) | 0x0A0A12);
        String title = selectedModule != null ? selectedModule.getName() : "Settings";
        ctx.drawString(textRenderer, title, x + PADDING, y + 9, accentColor);

        if (selectedModule == null) {
            ctx.drawCenteredString(textRenderer, "§7Select a module", x + RIGHT_W / 2, y + h / 2 - 4, textMuted);
            return;
        }

        int rowY = y + HEADER_H + 6;
        for (Module.Setting<?> setting : selectedModule.getSettings()) {
            ctx.drawString(textRenderer, "§7" + setting.getName(), x + PADDING, rowY, textMuted);
            rowY += 10;

            Object val = setting.getValue();
            if (val instanceof Boolean b) {
                drawPill(ctx, x + PADDING, rowY, b);
                rowY += 16;
            } else if (val instanceof Double d && setting.isSlider()) {
                double mn = (Double) setting.getMin(), mx2 = (Double) setting.getMax();
                float pct = (float)((d - mn) / (mx2 - mn));
                int trackW = RIGHT_W - PADDING * 2;
                ctx.fill(x + PADDING, rowY, x + PADDING + trackW, rowY + 6, 0xFF1A1A1A);
                ctx.fill(x + PADDING, rowY, x + PADDING + (int)(trackW * pct), rowY + 6, accentColor);
                ctx.drawString(textRenderer, String.format("%.1f", d), x + PADDING + trackW - 20, rowY - 2, textPrimary);
                rowY += 14;
            } else if (val instanceof String s) {
                int w = textRenderer.getWidth(s) + 6;
                ctx.fill(x + PADDING, rowY, x + PADDING + w, rowY + 12, 0x44FFFFFF);
                ctx.drawString(textRenderer, s, x + PADDING + 3, rowY + 2, textPrimary);
                rowY += 16;
            }
            rowY += 4;
        }

        // Panel opacity slider
        int sliderY = y + h - 40;
        ctx.drawString(textRenderer, "§7Panel Opacity", x + PADDING, sliderY, textMuted);
        sliderY += 10;
        float opacityPct = panelAlpha / 255f;
        int trackW = RIGHT_W - PADDING * 2;
        ctx.fill(x + PADDING, sliderY, x + PADDING + trackW, sliderY + 6, 0xFF1A1A1A);
        ctx.fill(x + PADDING, sliderY, x + PADDING + (int)(trackW * opacityPct), sliderY + 6, accentColor);
        ctx.drawString(textRenderer, panelAlpha + "/255", x + PADDING + trackW - 30, sliderY - 2, textPrimary);
    }

    // ── PILL TOGGLE ──────────────────────────────────────────────────
    private void drawPill(DrawContext ctx, int x, int y, boolean on) {
        int trackColor = on ? accentColor : 0xFF333333;
        ctx.fill(x, y, x + 24, y + 10, trackColor);
        ctx.fill(x + 1, y + 1, x + 23, y + 9, (on ? (accentColor & 0x00FFFFFF | 0x88000000) : 0x88000000));
        int knobX = on ? x + 14 : x + 2;
        ctx.fill(knobX, y + 1, knobX + 8, y + 9, 0xFFFFFFFF);
    }

    // ──────────────────────────────────────────────────────────────────
    //  MOUSE EVENTS
    // ──────────────────────────────────────────────────────────────────
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int totalW = LEFT_W + MID_W + RIGHT_W;
        int totalH = Math.min(height - 40, 320);
        int originX = (width - totalW) / 2;
        int originY = (height - totalH) / 2;
        int mx = (int) mouseX, my = (int) mouseY;

        // ── LEFT: category select
        if (mx >= originX && mx < originX + LEFT_W) {
            int rowY = originY + HEADER_H + 4;
            for (Module.Category cat : Module.Category.values()) {
                if (my >= rowY && my < rowY + ROW_H) {
                    selectedCategory = cat;
                    selectedModule = null;
                    scrollOffset = 0;
                    return true;
                }
                rowY += ROW_H;
            }
        }

        // ── MIDDLE: toggle or select
        if (mx >= originX + LEFT_W && mx < originX + LEFT_W + MID_W) {
            List<Module> mods = SpeckedClient.moduleManager.getByCategory(selectedCategory);
            int rowY = originY + HEADER_H + 4 - scrollOffset;
            for (Module m : mods) {
                if (my >= rowY && my < rowY + ROW_H) {
                    int pillX = originX + LEFT_W + MID_W - 32;
                    if (mx >= pillX && mx < pillX + 24) {
                        m.toggle(); // clicked pill
                    } else {
                        selectedModule = (selectedModule == m) ? null : m; // clicked row
                    }
                    return true;
                }
                rowY += ROW_H;
            }
        }

        // ── RIGHT: settings interactions
        if (selectedModule != null && mx >= originX + LEFT_W + MID_W && mx < originX + LEFT_W + MID_W + RIGHT_W) {
            int rx = originX + LEFT_W + MID_W;
            int rowY = originY + HEADER_H + 6;
            for (Module.Setting<?> setting : selectedModule.getSettings()) {
                rowY += 10;
                Object val = setting.getValue();
                if (val instanceof Boolean) {
                    if (my >= rowY && my < rowY + 16) {
                        @SuppressWarnings("unchecked")
                        Module.Setting<Boolean> bSetting = (Module.Setting<Boolean>) setting;
                        bSetting.setValue(!bSetting.getValue());
                        // If it's the "enabled" toggle apply it
                        return true;
                    }
                    rowY += 16;
                } else if (val instanceof Double && setting.isSlider()) {
                    if (my >= rowY && my < rowY + 6) {
                        int trackW = RIGHT_W - PADDING * 2;
                        float pct = Math.max(0, Math.min(1, (float)(mx - rx - PADDING) / trackW));
                        @SuppressWarnings("unchecked")
                        Module.Setting<Double> dSetting = (Module.Setting<Double>) setting;
                        double mn = dSetting.getMin(), mx2 = dSetting.getMax();
                        dSetting.setValue(mn + (mx2 - mn) * pct);
                        return true;
                    }
                    rowY += 14;
                } else if (val instanceof String) {
                    rowY += 16;
                }
                rowY += 4;
            }
            // Panel opacity slider
            int sliderY = originY + totalH - 40 + 10;
            if (my >= sliderY && my < sliderY + 6) {
                int trackW = RIGHT_W - PADDING * 2;
                float pct = Math.max(0, Math.min(1, (float)(mx - rx - PADDING) / trackW));
                panelAlpha = (int)(pct * 255);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Same logic as mouseClicked for sliders — re-use
        mouseClicked(mouseX, mouseY, button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int totalW = LEFT_W + MID_W + RIGHT_W;
        int originX = (width - totalW) / 2;
        if (mouseX >= originX + LEFT_W && mouseX < originX + LEFT_W + MID_W) {
            scrollOffset = Math.max(0, scrollOffset - (int)(verticalAmount * ROW_H));
        }
        return true;
    }

    // ──────────────────────────────────────────────────────────────────
    //  HELPERS
    // ──────────────────────────────────────────────────────────────────
    private String catIcon(Module.Category cat) {
        return switch (cat) {
            case COMBAT   -> "⚔";
            case HUD      -> "◈";
            case MOVEMENT -> "↑";
            case VISUAL   -> "◉";
            case UTILITY  -> "⚙";
        };
    }
}
