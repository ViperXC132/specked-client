package dev.pixelforge.specked.modules.hud;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorHudModule extends Module {

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHudModule() {
        super("Armor HUD", "Shows armor durability with enchant glint", Category.HUD);
        settings.add(new Setting<>("X Position", 4, 0, 500));
        settings.add(new Setting<>("Y Position", 4, 0, 500));
        settings.add(new Setting<>("Show Durability", true));
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (mc.player == null) return;

        int baseX = (int)(double)(Double) settings.get(0).getValue();
        int baseY = (int)(double)(Double) settings.get(1).getValue();
        boolean showDura = (boolean) settings.get(2).getValue();

        int y = baseY;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getEquippedStack(slot);
            if (stack.isEmpty() || stack.getItem() == Items.AIR) { y += 20; continue; }

            // Draw item with glint if enchanted
            ctx.drawItem(stack, baseX, y);
            ctx.drawItemInSlot(mc.textRenderer, stack, baseX, y);

            // Enchant indicator dot
            boolean enchanted = isEnchanted(stack);
            if (enchanted) {
                ctx.fill(baseX + 14, y, baseX + 16, y + 2, 0xFF55FFFF);
            }

            // Durability bar
            if (showDura && stack.isDamageable()) {
                int maxDura = stack.getMaxDamage();
                int curDura = maxDura - stack.getDamage();
                float pct = (float) curDura / maxDura;
                int barW = (int)(14 * pct);
                int color = pct > 0.5f ? 0xFF55FF55 : pct > 0.25f ? 0xFFFFFF55 : 0xFFFF5555;
                ctx.fill(baseX, y + 17, baseX + 14, y + 18, 0xFF1A1A1A);
                ctx.fill(baseX, y + 17, baseX + barW, y + 18, color);
            }

            y += 20;
        }
    }

    private boolean isEnchanted(ItemStack stack) {
        ItemEnchantmentsComponent enc = stack.get(DataComponentTypes.ENCHANTMENTS);
        return enc != null && !enc.isEmpty();
    }
}
