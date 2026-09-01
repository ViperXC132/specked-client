package dev.pixelforge.specked.modules.hud;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ShieldStatusModule extends Module {

    public ShieldStatusModule() {
        super("Shield Status", "Shows shield cooldown and blocking state", Category.HUD);
        settings.add(new Setting<>("X Position", 4.0, 0.0, 500.0));
        settings.add(new Setting<>("Y Position", 200.0, 0.0, 500.0));
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (mc.player == null) return;
        ItemStack shield = mc.player.getMainHandStack().getItem() == Items.SHIELD
            ? mc.player.getMainHandStack() : mc.player.getOffHandStack();
        if (shield.getItem() != Items.SHIELD) return;

        int x = ((Double) settings.get(0).getValue()).intValue();
        int y = ((Double) settings.get(1).getValue()).intValue();

        boolean blocking = mc.player.isBlocking();
        int cd = mc.player.getItemCooldownManager().getCooldownProgress(shield, tickDelta) > 0 ? 1 : 0;

        String status;
        int color;
        if (cd > 0) {
            status = "Shield: Cooldown";
            color = 0xFFFF5555;
        } else if (blocking) {
            status = "Shield: Blocking";
            color = 0xFF55FF55;
        } else {
            status = "Shield: Ready";
            color = 0xFFFFFFFF;
        }

        int w = mc.textRenderer.getWidth(status);
        ctx.fill(x - 2, y - 2, x + w + 2, y + 10, 0x88000000);
        ctx.drawTextWithShadow(mc.textRenderer, status, x, y, color);
    }
}
