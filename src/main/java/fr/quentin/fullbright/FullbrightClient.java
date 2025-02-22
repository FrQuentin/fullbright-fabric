package fr.quentin.fullbright;

import fr.quentin.fullbright.command.FullbrightCommand;
import fr.quentin.fullbright.overlay.FullbrightOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Client-side initializer for the Fullbright mod.
 * Handles registration of client-specific features like commands, overlays and tick events.
 */
public class FullbrightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FullbrightCommand.register();
        FullbrightOverlay.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                FullbrightCommand.applyFullbrightEffect(client.player);
            }
        });
    }
}
