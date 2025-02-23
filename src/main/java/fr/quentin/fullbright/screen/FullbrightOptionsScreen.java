package fr.quentin.fullbright.screen;

import fr.quentin.fullbright.Fullbright;
import fr.quentin.fullbright.config.FullbrightConfig;
import fr.quentin.fullbright.command.FullbrightCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/**
 * Custom screen for configuring Fullbright mod options.
 * Extends GameOptionsScreen to integrate with Minecraft's options menu.
 */
@Environment(EnvType.CLIENT)
public class FullbrightOptionsScreen extends GameOptionsScreen {
    /**
     * The title displayed at the top of the screen.
     * Uses a translatable text component for localization.
     */
    private static final Text TITLE = Text.translatable("fullbright.screen.configuration");

    /**
     * The configuration instance for the mod.
     * Used to get and set configuration options.
     */
    private final FullbrightConfig config;

    /**
     * Constructs a new FullbrightOptionsScreen.
     *
     * @param parent The parent screen to return to when this screen is closed.
     * @param gameOptions The game options instance.
     */
    public FullbrightOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE);
        this.config = FullbrightCommand.getConfig(); // Load the current configuration
    }

    /**
     * Gets the options to display on the screen.
     * Each option is represented as a SimpleOption.
     *
     * @param config The configuration instance to bind the options to.
     * @return An array of SimpleOption objects representing the configurable options.
     */
    private static SimpleOption<?>[] getOptions(FullbrightConfig config) {
        return new SimpleOption[]{
                // Option to enable/disable the fullbright effect
                new SimpleOption<>(
                        "fullbright.option.enabled", // Translation key for the option name
                        SimpleOption.emptyTooltip(), // No tooltip
                        (text, value) -> value ? Text.translatable("fullbright.button.on") : Text.translatable("fullbright.button.off"), // Display text
                        SimpleOption.BOOLEAN, // Option type (boolean toggle)
                        config.isEnabled(), // Current value
                        value -> {
                            config.setEnabled(value); // Update the configuration
                            config.save(); // Save the configuration
                        }
                ),
                // Option to show/hide the overlay
                new SimpleOption<>(
                        "fullbright.option.overlay", // Translation key for the option name
                        SimpleOption.emptyTooltip(), // No tooltip
                        (text, value) -> value ? Text.translatable("fullbright.button.on") : Text.translatable("fullbright.button.off"), // Display text
                        SimpleOption.BOOLEAN, // Option type (boolean toggle)
                        config.isShowOverlay(), // Current value
                        value -> {
                            config.setShowOverlay(value); // Update the configuration
                            config.save(); // Save the configuration
                        }
                )
        };
    }

    /**
     * Adds the options to the screen.
     * Called when the screen is initialized.
     */
    @Override
    protected void addOptions() {
        if (this.body != null) {  // Check for null to avoid NullPointerException
            SimpleOption<?>[] options = getOptions(this.config);
            this.body.addAll(options); // Add the options to the screen's body
        } else {
            Fullbright.LOGGER.error("Failed to add options: option list is null");
        }
    }

    /**
     * Called when the screen is removed from view.
     * Ensures the configuration is saved when the screen is closed.
     */
    @Override
    public void removed() {
        this.config.save(); // Save the configuration
        super.removed(); // Call the superclass method
    }

    /**
     * Determines whether the game should be paused when this screen is open.
     *
     * @return False, indicating the game should not pause.
     */
    @Override
    public boolean shouldPause() {
        return false; // The game should not pause when this screen is open
    }
}
