package fr.quentin.fullbright.command;

import fr.quentin.fullbright.config.FullbrightConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

/**
 * Manages the Fullbright command functionality.
 * Provides commands to toggle fullbright effect and overlay visibility.
 */
public class FullbrightCommand {
    /**
     * The configuration instance for the mod.
     * Loaded once and reused throughout the mod's lifecycle.
     */
    private static final FullbrightConfig CONFIG = FullbrightConfig.load();

    /**
     * Registers all command-related functionality.
     * This includes the main command and all its subcommands.
     */
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("fullbright")
                    .executes(context -> {
                        context.getSource().sendFeedback(Text.translatable("fullbright.command.hint"));
                        return 1;
                    })
                    .then(ClientCommandManager.literal("on")
                            .executes(context -> {
                                if (!CONFIG.isEnabled()) {
                                    CONFIG.setEnabled(true);
                                    context.getSource().sendFeedback(Text.translatable("fullbright.on"));
                                } else {
                                    context.getSource().sendFeedback(Text.translatable("fullbright.already_on"));
                                }
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("off")
                            .executes(context -> {
                                if (CONFIG.isEnabled()) {
                                    CONFIG.setEnabled(false);
                                    context.getSource().sendFeedback(Text.translatable("fullbright.off"));
                                } else {
                                    context.getSource().sendFeedback(Text.translatable("fullbright.already_off"));
                                }
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("overlay")
                            .then(ClientCommandManager.literal("on")
                                    .executes(context -> {
                                        if (!CONFIG.isShowOverlay()) {
                                            CONFIG.setShowOverlay(true);
                                            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.on"));
                                        } else {
                                            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.already_on"));
                                        }
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("off")
                                    .executes(context -> {
                                        if (CONFIG.isShowOverlay()) {
                                            CONFIG.setShowOverlay(false);
                                            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.off"));
                                        } else {
                                            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.already_off"));
                                        }
                                        return 1;
                                    })))
                    .then(ClientCommandManager.literal("help")
                            .executes(context -> {
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.title"));
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.description"));
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.toggle"));
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.on"));
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.off"));
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.overlay.on"));
                                context.getSource().sendFeedback(Text.translatable("fullbright.help.overlay.off"));
                                return 1;
                            })));
        });
    }

    /**
     * Applies or removes the night vision effect based on the mod's enabled state.
     *
     * @param player The player to apply the effect to
     */
    public static void applyFullbrightEffect(ClientPlayerEntity player) {
        if (CONFIG.isEnabled()) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION,
                    300,  // Duration in ticks (15 seconds)
                    0, // Amplifier (level 1)
                    false, // Show particles
                    false, // Show icon
                    false  // Is ambient
            ));
        } else {
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    /**
     * Gets the current configuration instance.
     *
     * @return The mod's configuration
     */
    public static FullbrightConfig getConfig() {
        return CONFIG;
    }
}
