package fr.quentin.fullbright.overlay;

import fr.quentin.fullbright.Fullbright;
import fr.quentin.fullbright.command.FullbrightCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;

/**
 * Handles the rendering of the Fullbright status overlay.
 * Displays an icon and text when the fullbright effect is active.
 */
public class FullbrightOverlay {
    /**
     * The texture used for the overlay icon.
     * This texture is loaded from the mod's resources.
     */
    private static final Identifier TEXTURE = Identifier.of(Fullbright.MOD_ID, "textures/gui/fullbright.png");

    /**
     * The size of the icon in pixels.
     * Defines the width and height of the icon displayed on the screen.
     */
    private static final int ICON_SIZE = 16;

    /**
     * The margin from the screen edge in pixels.
     * Defines the space between the icon and the edge of the screen.
     */
    private static final int MARGIN = 5;

    /**
     * The padding between the icon and text in pixels.
     * Defines the space between the icon and the status text.
     */
    private static final int TEXT_PADDING = 4;

    /**
     * Registers the overlay renderer with the game's HUD system.
     * The overlay will only be rendered when the mod and overlay are enabled.
     */
    public static void register() {
        // Register the overlay renderer with the HUD rendering system
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            Identifier statusOverlay = Identifier.of(Fullbright.MOD_ID, "status_overlay");
            layeredDrawer.attachLayerAfter(
                    IdentifiedLayer.MISC_OVERLAYS, // Attach after the miscellaneous overlays
                    statusOverlay,
                    (context, tickCounter) -> {
                        // Check if the fullbright effect and overlay are enabled
                        if (FullbrightCommand.getConfig().isEnabled() &&
                                FullbrightCommand.getConfig().isShowOverlay()) {
                            renderOverlay(context); // Render the overlay
                        }
                    }
            );
        });
    }

    /**
     * Renders the overlay to the screen.
     * Draws both the icon texture and status text.
     *
     * @param context The drawing context provided by the game
     */
    private static void renderOverlay(DrawContext context) {
        // Draw the icon texture on the screen
        context.drawTexture(
                RenderLayer::getGuiTextured, // Render layer for GUI textures
                TEXTURE, // Texture to draw
                MARGIN, // X position
                MARGIN, // Y position
                0f, // UV coordinates start (U)
                0f, // UV coordinates start (V)
                ICON_SIZE, // Width of the region to draw
                ICON_SIZE, // Height of the region to draw
                ICON_SIZE, // Total texture width
                ICON_SIZE  // Total texture height
        );

        // Draw the status text next to the icon
        int textX = MARGIN + ICON_SIZE + TEXT_PADDING; // X position for the text
        int textY = MARGIN + (ICON_SIZE - 8) / 2; // Y position for the text (centered vertically)

        context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer, // Text renderer
                Text.translatable("fullbright.name"), // Text to draw
                textX, // X position
                textY, // Y position
                0xFFFFFFFF // Text color (white with full opacity)
        );
    }
}
