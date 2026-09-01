package dev.pixelforge.specked.modules;

import dev.pixelforge.specked.modules.combat.*;
import dev.pixelforge.specked.modules.hud.*;
import dev.pixelforge.specked.modules.movement.*;
import dev.pixelforge.specked.modules.visual.*;
import dev.pixelforge.specked.modules.utility.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // HUD
        register(new ArmorHudModule());
        register(new FpsCounterModule());
        register(new CpsCounterModule());
        register(new PotionTimerModule());
        register(new ShieldStatusModule());
        register(new KeystrokesModule());

        // Combat
        register(new ShieldFixesModule());
        register(new PvpEssentialsModule());

        // Visual
        register(new CrosshairModule());
        register(new SkinLayers3DModule());
        register(new ZoomModule());

        // Movement
        register(new ToggleSprintModule());

        // Utility
        register(new ShulkerTooltipModule());
        register(new AppleskinModule());
    }

    private void register(Module m) { modules.add(m); }

    public List<Module> getAll() { return modules; }

    public List<Module> getByCategory(Module.Category cat) {
        return modules.stream().filter(m -> m.getCategory() == cat).collect(Collectors.toList());
    }

    public void onTick(MinecraftClient client) {
        for (Module m : modules) if (m.isEnabled()) m.onTick(client);
    }

    public void onHudRender(DrawContext ctx, float tickDelta) {
        for (Module m : modules) if (m.isEnabled()) m.onHudRender(ctx, tickDelta);
    }
}
