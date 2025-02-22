package fr.quentin.fullbright.overlay;

import fr.quentin.fullbright.Fullbright;
import fr.quentin.fullbright.config.FullbrightConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;

public class FullbrightOverlay {
    private static final Identifier TEXTURE = Identifier.of(Fullbright.MOD_ID, "textures/gui/fullbright.png");
    private static final int ICON_SIZE = 16;
    private static final int MARGIN = 5;
    private static final int TEXT_PADDING = 4;
    private static FullbrightConfig config;

    public static void register(FullbrightConfig fullbrightConfig) {
        config = fullbrightConfig;
        Identifier layerId = Identifier.of(Fullbright.MOD_ID, "status_overlay");

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer ->
                layeredDrawer.attachLayerAfter(
                        IdentifiedLayer.MISC_OVERLAYS,
                        layerId,
                        (context, tickCounter) -> {
                            if (config.isEnabled()) {
                                renderOverlay(context);
                            }
                        }
                )
        );
    }

    private static void renderOverlay(DrawContext context) {
        if (!config.isShowOverlay()) {
            return;
        }

        context.drawTexture(
                RenderLayer::getGuiTextured,
                TEXTURE,
                MARGIN,
                MARGIN,
                0,
                0,
                ICON_SIZE,
                ICON_SIZE,
                ICON_SIZE,
                ICON_SIZE,
                ICON_SIZE,
                ICON_SIZE
        );

        int textX = MARGIN + ICON_SIZE + TEXT_PADDING;
        int textY = MARGIN + (ICON_SIZE - 8) / 2;

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.translatable("fullbright.name"),
                textX,
                textY,
                0xFFFFFFFF,
                true
        );
    }
}
