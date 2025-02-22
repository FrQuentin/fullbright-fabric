package fr.quentin.fullbright;

import fr.quentin.fullbright.command.FullbrightCommand;
import fr.quentin.fullbright.overlay.FullbrightOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FullbrightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FullbrightCommand.initialize();
        FullbrightCommand.register();

        FullbrightOverlay.register(FullbrightCommand.getConfig());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FullbrightCommand.applyFullbrightEffect();
        });
    }
}
