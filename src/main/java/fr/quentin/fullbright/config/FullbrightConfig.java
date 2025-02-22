package fr.quentin.fullbright.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.quentin.fullbright.Fullbright;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Configuration management for the Fullbright mod.
 * Handles saving and loading of mod settings using JSON format.
 */
public class FullbrightConfig {
    /**
     * The location where the config file will be stored.
     */
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("fullbright.json").toFile();

    /**
     * Gson instance for JSON serialization/deserialization.
     * Configured to create human-readable JSON files.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Whether the fullbright effect is currently enabled.
     */
    private boolean enabled = false;

    /**
     * Whether the status overlay should be displayed.
     */
    private boolean showOverlay = true;

    /**
     * Saves the current configuration to disk.
     * If an error occurs during saving, it will be logged but won't crash the game.
     */
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            Fullbright.LOGGER.error("Error saving Fullbright configuration", e);
        }
    }

    /**
     * Loads the configuration from disk or creates a new one if none exists.
     *
     * @return The loaded or newly created configuration
     */
    public static FullbrightConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, FullbrightConfig.class);
            } catch (IOException e) {
                Fullbright.LOGGER.error("Error loading Fullbright configuration", e);
            }
        }

        FullbrightConfig config = new FullbrightConfig();
        config.save();
        return config;
    }

    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public boolean isShowOverlay() {
        return showOverlay;
    }

    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
        save();
    }
}
