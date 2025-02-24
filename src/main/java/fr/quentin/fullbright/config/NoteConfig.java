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
 * Configuration management for storing and retrieving a note.
 * Handles saving and loading of the note using JSON format.
 */
public class NoteConfig {
    /**
     * Singleton instance of the configuration.
     * Ensures that only one instance of the configuration is used throughout the application.
     */
    private static NoteConfig instance;

    /**
     * The location where the config file will be stored.
     * Uses the Fabric loader's config directory to resolve the path.
     */
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("note.json").toFile();

    /**
     * Gson instance for JSON serialization/deserialization.
     * Configured to create human-readable JSON files with pretty printing.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Maximum length of the note in characters.
     * Ensures that the note does not exceed a certain length to prevent excessive storage usage.
     */
    private static final int MAX_NOTE_LENGTH = 10000;

    /**
     * The note content.
     * Defaults to an empty string.
     */
    private String note = "";

    /**
     * Loads the configuration from disk or creates a new one if none exists.
     *
     * @return The loaded or newly created configuration
     */
    public static NoteConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, NoteConfig.class); // Deserialize the JSON file to a configuration object
                if (instance == null) {
                    instance = new NoteConfig(); // Create a new instance if deserialization fails
                }
                return instance;
            } catch (IOException e) {
                Fullbright.LOGGER.error("Error loading NoteConfig", e);
            }
        }

        // If the config file does not exist, create a new configuration instance
        instance = new NoteConfig();
        instance.save(); // Save the default configuration to disk
        return instance;
    }

    /**
     * Gets the singleton instance of the configuration.
     * If the instance is null, it loads the configuration from disk.
     *
     * @return The singleton instance of the configuration
     */
    public static NoteConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    /**
     * Gets the current note content.
     *
     * @return The note content
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note content.
     * If the note exceeds the maximum length, it is truncated.
     *
     * @param note The new note content
     */
    public void setNote(String note) {
        if (note == null) {
            this.note = ""; // Set to empty string if the input is null
        } else if (note.length() > MAX_NOTE_LENGTH) {
            this.note = note.substring(0, MAX_NOTE_LENGTH); // Truncate the note if it exceeds the maximum length
            Fullbright.LOGGER.warn("Note truncated to " + MAX_NOTE_LENGTH + " characters");
        } else {
            this.note = note; // Set the note content
        }
        save(); // Save the configuration whenever the note changes
    }

    /**
     * Saves the current configuration to disk.
     * If an error occurs during saving, it will be logged but won't crash the game.
     */
    public void save() {
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.getParentFile().mkdirs(); // Create parent directories if they don't exist
                CONFIG_FILE.createNewFile(); // Create the file if it doesn't exist
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer); // Serialize the current configuration to JSON and write to file
            }
        } catch (IOException e) {
            Fullbright.LOGGER.error("Error saving NoteConfig", e);
        }
    }
}
