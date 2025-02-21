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
                    .executes(FullbrightCommand::toggleFullbright)
                    .then(ClientCommandManager.literal("on")
                            .executes(FullbrightCommand::enableFullbright))
                    .then(ClientCommandManager.literal("off")
                            .executes(FullbrightCommand::disableFullbright))
                    .then(ClientCommandManager.literal("help")
                            .executes(FullbrightCommand::displayHelp))
            );
        });
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

    private static int toggleFullbright(CommandContext<FabricClientCommandSource> context) {
        config.setEnabled(!config.isEnabled());
        if (config.isEnabled()) {
            context.getSource().sendFeedback(Text.translatable("fullbright.on"));
        } else {
            context.getSource().sendFeedback(Text.translatable("fullbright.off"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int displayHelp(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.translatable("fullbright.help.title"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.description"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.toggle"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.on"));
        context.getSource().sendFeedback(Text.translatable("fullbright.help.off"));
        return Command.SINGLE_SUCCESS;
    }

    public static void applyFullbrightEffect() {
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (config.isEnabled()) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        200,
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
}
