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
     * Singleton instance of the configuration.
     * Ensures that only one instance of the configuration is used throughout the application.
     */
    private static FullbrightConfig instance;

    /**
     * The location where the config file will be stored.
     * Uses the Fabric loader's config directory to resolve the path.
     */
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("fullbright.json").toFile();

    /**
     * Gson instance for JSON serialization/deserialization.
     * Configured to create human-readable JSON files with pretty printing.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Whether the fullbright effect is currently enabled.
     * Defaults to false.
     */
    private boolean enabled = false;

    /**
     * Whether the status overlay should be displayed.
     * Defaults to true.
     */
    private boolean showOverlay = true;

    /**
     * Saves the current configuration to disk.
     * If an error occurs during saving, it will be logged but won't crash the game.
     */
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer); // Serialize the current configuration to JSON and write to file
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
                instance = GSON.fromJson(reader, FullbrightConfig.class); // Deserialize the JSON file to a configuration object
                return instance;
            } catch (IOException e) {
                Fullbright.LOGGER.error("Error loading Fullbright configuration", e);
            }
        }

        // If the config file does not exist, create a new configuration instance
        instance = new FullbrightConfig();
        instance.save(); // Save the default configuration to disk
        return instance;
    }

    /**
     * Gets the singleton instance of the configuration.
     * If the instance is null, it loads the configuration from disk.
     *
     * @return The singleton instance of the configuration
     */
    public static FullbrightConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    /**
     * Checks if the fullbright effect is enabled.
     *
     * @return True if the fullbright effect is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the fullbright effect is enabled.
     *
     * @param enabled True to enable the fullbright effect, false to disable it
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save(); // Save the configuration whenever the enabled state changes
    }

    /**
     * Checks if the status overlay should be displayed.
     *
     * @return True if the status overlay should be displayed, false otherwise
     */
    public boolean isShowOverlay() {
        return showOverlay;
    }

    /**
     * Sets whether the status overlay should be displayed.
     *
     * @param showOverlay True to display the status overlay, false to hide it
     */
    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
        save(); // Save the configuration whenever the overlay visibility changes
    }
}
