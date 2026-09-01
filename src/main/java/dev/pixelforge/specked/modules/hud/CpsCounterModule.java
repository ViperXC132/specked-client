package dev.pixelforge.specked.modules.hud;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayDeque;
import java.util.Deque;

public class CpsCounterModule extends Module {
    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();
    public CpsCounterModule() {
        super("CPS Counter", "Shows clicks per second", Category.HUD);
        settings.add(new Setting<>("X Position", 4.0, 0.0, 500.0));
        settings.add(new Setting<>("Y Position", 102.0, 0.0, 500.0));
    }
    public void registerLeft() { leftClicks.addLast(System.currentTimeMillis()); }
    public void registerRight() { rightClicks.addLast(System.currentTimeMillis()); }
    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        int x = ((Double) settings.get(0).getValue()).intValue();
        int y = ((Double) settings.get(1).getValue()).intValue();
        long now = System.currentTimeMillis();
        leftClicks.removeIf(t -> now - t > 1000);
        rightClicks.removeIf(t -> now - t > 1000);
        String text = "CPS: " + leftClicks.size() + "L " + rightClicks.size() + "R";
        int w = mc.textRenderer.getWidth(text);
        ctx.fill(x - 2, y - 2, x + w + 2, y + 10, 0x88000000);
        ctx.drawTextWithShadow(mc.textRenderer, text, x, y, 0xFFFFFFFF);
    }
}
