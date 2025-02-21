package fr.quentin.fullbright.config;

import fr.quentin.fullbright.Fullbright;
import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FullbrightConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fullbright.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            Fullbright.LOGGER.error("Error saving Fullbright configuration: {}", e.getMessage());
        }
    }

    public static FullbrightConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, FullbrightConfig.class);
            } catch (IOException e) {
                Fullbright.LOGGER.error("Error loading Fullbright configuration: {}", e.getMessage());
            }
        }

        FullbrightConfig config = new FullbrightConfig();
        config.save();
        return config;
    }
}
