package fr.quentin.fullbright.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import fr.quentin.fullbright.config.FullbrightConfig;

public class FullbrightCommand {
    private static FullbrightConfig config;

    public static void initialize() {
        config = FullbrightConfig.load();
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("fullbright")
                    .executes(context -> {
                        context.getSource().sendFeedback(Text.translatable("fullbright.command.hint"));
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(ClientCommandManager.literal("on")
                            .executes(FullbrightCommand::enableFullbright))
                    .then(ClientCommandManager.literal("off")
                            .executes(FullbrightCommand::disableFullbright))
                    .then(ClientCommandManager.literal("help")
                            .executes(FullbrightCommand::displayHelp))
                    .then(ClientCommandManager.literal("overlay")
                            .then(ClientCommandManager.literal("on")
                                    .executes(FullbrightCommand::enableOverlay))
                            .then(ClientCommandManager.literal("off")
                                    .executes(FullbrightCommand::disableOverlay))
                    )
            );
        });
    }

    private static int enableOverlay(CommandContext<FabricClientCommandSource> context) {
        if (!config.isShowOverlay()) {
            config.setShowOverlay(true);
            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.on"));
        } else {
            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.already_on"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int disableOverlay(CommandContext<FabricClientCommandSource> context) {
        if (config.isShowOverlay()) {
            config.setShowOverlay(false);
            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.off"));
        } else {
            context.getSource().sendFeedback(Text.translatable("fullbright.overlay.already_off"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int enableFullbright(CommandContext<FabricClientCommandSource> context) {
        if (!config.isEnabled()) {
            config.setEnabled(true);
            context.getSource().sendFeedback(Text.translatable("fullbright.on"));
        } else {
            context.getSource().sendFeedback(Text.translatable("fullbright.already_on"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int disableFullbright(CommandContext<FabricClientCommandSource> context) {
        if (config.isEnabled()) {
            config.setEnabled(false);
            context.getSource().sendFeedback(Text.translatable("fullbright.off"));
        } else {
            context.getSource().sendFeedback(Text.translatable("fullbright.already_off"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int displayHelp(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.translatable("fullbright.help.title"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.description"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.toggle"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.on"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.off"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.overlay.on"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.overlay.off"));
        return Command.SINGLE_SUCCESS;
    }

    public static void applyFullbrightEffect() {
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (config.isEnabled()) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        300,
                        0,
                        false,
                        false,
                        false
                ));
            } else {
                player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }

    public static FullbrightConfig getConfig() {
        return config;
    }
}
