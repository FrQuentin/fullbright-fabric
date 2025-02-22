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
     */
    private static final Identifier TEXTURE = Identifier.of(Fullbright.MOD_ID, "textures/gui/fullbright.png");

    /**
     * The size of the icon in pixels.
     */
    private static final int ICON_SIZE = 16;

    /**
     * The margin from the screen edge in pixels.
     */
    private static final int MARGIN = 5;

    /**
     * The padding between the icon and text in pixels.
     */
    private static final int TEXT_PADDING = 4;

    /**
     * Registers the overlay renderer with the game's HUD system.
     * The overlay will only be rendered when the mod and overlay are enabled.
     */
    public static void register() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            Identifier statusOverlay = Identifier.of(Fullbright.MOD_ID, "status_overlay");
            layeredDrawer.attachLayerAfter(
                    IdentifiedLayer.MISC_OVERLAYS,
                    statusOverlay,
                    (context, tickCounter) -> {
                        if (FullbrightCommand.getConfig().isEnabled() &&
                                FullbrightCommand.getConfig().isShowOverlay()) {
                            renderOverlay(context);
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
        // Draw the icon texture
        context.drawTexture(
                RenderLayer::getGuiTextured,
                TEXTURE,
                MARGIN,
                MARGIN,
                0f, // UV coordinates start
                0f,
                ICON_SIZE, // Width of the region to draw
                ICON_SIZE, // Height of the region to draw
                ICON_SIZE, // Total texture width
                ICON_SIZE  // Total texture height
        );

        // Draw the status text
        int textX = MARGIN + ICON_SIZE + TEXT_PADDING;
        int textY = MARGIN + (ICON_SIZE - 8) / 2; // Centered vertically relative to the icon

        context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                Text.translatable("fullbright.name"),
                textX,
                textY,
                0xFFFFFFFF // White color with full opacity
        );
    }
}
