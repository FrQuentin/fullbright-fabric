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
        // Registers the command with the Fabric command system
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("fullbright")
                    // Main command, provides a hint when executed without arguments
                    .executes(context -> {
                        context.getSource().sendFeedback(Text.translatable("fullbright.command.hint"));
                        return 1;
                    })
                    // Subcommand to enable the fullbright effect
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
                    // Subcommand to disable the fullbright effect
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
                    // Subcommand to manage the overlay visibility
                    .then(ClientCommandManager.literal("overlay")
                            // Enable overlay
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
                            // Disable overlay
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
                    // Help command to display usage information
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
            // Apply the night vision effect with a long duration
            StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (currentEffect == null || currentEffect.getDuration() < 210) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        -1, // Duration in ticks (1 tick = 1/20 second)
                        0,   // Amplifier (level of the effect)
                        false, // Ambient (whether the effect is ambient)
                        false, // Visible (whether the effect is visible)
                        false // Show icon (whether to show the effect icon)
                ));
            }
        } else {
            // Forcefully remove the night vision effect
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
