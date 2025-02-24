package fr.quentin.fullbright;

import fr.quentin.fullbright.command.FullbrightCommand;
import fr.quentin.fullbright.config.NoteConfig;
import fr.quentin.fullbright.option.KeyBindings;
import fr.quentin.fullbright.overlay.FullbrightOverlay;
import fr.quentin.fullbright.screen.FullbrightOptionsScreen;
import fr.quentin.fullbright.screen.SimpleTextEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Client-side initializer for the Fullbright mod.
 * Handles registration of client-specific features like commands, overlays, and tick events.
 */
public class FullbrightClient implements ClientModInitializer {
    /**
     * Called when the client-side part of the mod is initialized.
     * This method is part of the Fabric mod lifecycle and is where client-specific features should be set up.
     */
    @Override
    public void onInitializeClient() {
        // Register commands specific to the Fullbright mod
        FullbrightCommand.register();

        // Register the overlay that displays the Fullbright status
        FullbrightOverlay.register();

        // Register key bindings for the mod
        KeyBindings.register();

        // Register an event listener for client tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Apply the Fullbright effect to the player if enabled
            if (client.player != null) {
                FullbrightCommand.applyFullbrightEffect(client.player);
            }

            // Check if the configuration key binding was pressed
            if (client.player != null && KeyBindings.configurationKey.wasPressed()) {
                // Open the Fullbright configuration screen
                client.setScreen(new FullbrightOptionsScreen(client.currentScreen, client.options));
            }

            // Check if the text editor key binding was pressed
            if (client.player != null && KeyBindings.textEditorKey.wasPressed()) {
                // Open the SimpleTextEditorScreen with the current note
                client.setScreen(new SimpleTextEditorScreen(NoteConfig.getInstance().getNote()));
            }
        });
    }
}
