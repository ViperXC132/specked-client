package dev.pixelforge.specked.modules.utility;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;

public class AppleskinModule extends Module {

    public AppleskinModule() {
        super("AppleSkin", "Shows food hunger/saturation values", Category.UTILITY);
        settings.add(new Setting<>("Show Saturation", true));
        settings.add(new Setting<>("Show Hunger Restore", true));
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (mc.player == null) return;
        ItemStack held = mc.player.getMainHandStack();
        FoodComponent food = held.get(DataComponentTypes.FOOD);
        if (food == null) return;

        boolean showSat = (boolean) settings.get(0).getValue();
        boolean showHunger = (boolean) settings.get(1).getValue();

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int x = sw / 2 + 10;
        int y = sh - 39;

        if (showHunger) {
            String h = "+" + food.nutrition() + " 🍖";
            ctx.fill(x - 2, y - 2, x + mc.textRenderer.getWidth(h) + 2, y + 10, 0x88000000);
            ctx.drawTextWithShadow(mc.textRenderer, h, x, y, 0xFFFFCC44);
            y += 12;
        }
        if (showSat) {
            String s = "+" + String.format("%.1f", food.saturation()) + " sat";
            ctx.fill(x - 2, y - 2, x + mc.textRenderer.getWidth(s) + 2, y + 10, 0x88000000);
            ctx.drawTextWithShadow(mc.textRenderer, s, x, y, 0xFFFF9944);
        }
    }
}
