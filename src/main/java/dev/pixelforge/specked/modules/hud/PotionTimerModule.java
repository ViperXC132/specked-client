package dev.pixelforge.specked.modules.hud;

import dev.pixelforge.specked.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class PotionTimerModule extends Module {

    public PotionTimerModule() {
        super("Potion Timers", "Shows active potion effect durations", Category.HUD);
        settings.add(new Setting<>("X Position", 4.0, 0.0, 500.0));
        settings.add(new Setting<>("Y Position", 120.0, 0.0, 500.0));
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (mc.player == null) return;
        int x = ((Double) settings.get(0).getValue()).intValue();
        int startY = ((Double) settings.get(1).getValue()).intValue();

        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        int y = startY;
        for (StatusEffectInstance effect : effects) {
            RegistryEntry<StatusEffect> type = effect.getEffectType();
            String name = type.value().getName().getString();
            int dur = effect.getDuration();
            int secs = dur / 20;
            String time = String.format("%d:%02d", secs / 60, secs % 60);
            String label = name + " " + (effect.getAmplifier() + 1) + " §7" + time;
            int w = mc.textRenderer.getWidth(label);
            ctx.fill(x - 2, y - 2, x + w + 2, y + 10, 0x88000000);
            ctx.drawString(mc.textRenderer, label, x, y, 0xFFFFFFFF);
            y += 12;
        }
    }
}
