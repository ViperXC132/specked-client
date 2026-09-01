package dev.pixelforge.specked.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class SpeckedConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("specked.json");

    public int accentColor = 0xFF4FC3F7;
    public int panelAlpha = 0xB0;

    public static SpeckedConfig load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader r = new FileReader(CONFIG_PATH.toFile())) {
                return GSON.fromJson(r, SpeckedConfig.class);
            } catch (Exception e) {
                return new SpeckedConfig();
            }
        }
        return new SpeckedConfig();
    }

    public void save() {
        try (Writer w = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
