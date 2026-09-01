package dev.pixelforge.specked.modules;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    public enum Category {
        COMBAT, HUD, MOVEMENT, VISUAL, UTILITY
    }

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    // Settings
    protected final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient client) {}
    public void onHudRender(net.minecraft.client.gui.DrawContext ctx, float tickDelta) {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public List<Setting<?>> getSettings() { return settings; }

    // --- Setting inner class ---
    public static class Setting<T> {
        private final String name;
        private T value;
        private final T min, max; // null for non-numeric

        public Setting(String name, T value) {
            this.name = name;
            this.value = value;
            this.min = null;
            this.max = null;
        }

        public Setting(String name, T value, T min, T max) {
            this.name = name;
            this.value = value;
            this.min = min;
            this.max = max;
        }

        public String getName() { return name; }
        public T getValue() { return value; }
        public void setValue(T v) { this.value = v; }
        public T getMin() { return min; }
        public T getMax() { return max; }
        public boolean isSlider() { return min != null && max != null; }
    }
}
