/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.command.commands;


import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public final class ReloadCommand extends BaseCommand {
    ComponentLogger logger = TownyPlusMain.plugin.logger;
    public ReloadCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("reload").meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize("Reload the plugin's configuration"))
                        .permission(Constants.RELOAD_PERMISSION)
                        .handler(context -> this.commandManager.taskRecipe()
                                .begin(context)
                                .synchronous(c -> {
                                    this.execute(c);
                                })
                                .execute(() -> context.getSender().sendMessage("Reload done!"))
                        ));
    }

    private void execute(final @NonNull CommandContext<CommandSender> context) {
        Audience sender = plugin.adventure().sender(context.getSender());
        try {
            Config.reload();
            Lang.reload();
            if (this.plugin.chatHook != null) {
                this.plugin.chatHook.reload();
            }
            if (this.plugin.expansion != null) {
                Boolean didRegisterSuccessfully = this.plugin.expansion.register();
                if (didRegisterSuccessfully) {
                    logger.info("Successfully registered with PlaceholderAPI!");
                }
                else {
                    logger.warn("Failed to register with PlaceholderAPI!");
                }
                this.plugin.expansion.unregister();
            }
            if (this.plugin.restAPI != null && this.plugin.restAPI.active) {
                this.plugin.restAPI.stopServer();
            }
            if (Config.HTTPD_ENABLED) this.plugin.restAPI.startServer(Config.HTTPD_BIND, Config.HTTPD_PORT);

            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<rainbow><plugin> has successfully reloaded!</rainbow>",
                    Placeholder.unparsed("plugin", plugin.getName())
            ));
        } catch (Exception e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Whilst attempting to reload <plugin>, the plugin ran into errors. Check your console.</red>",
                    Placeholder.unparsed("plugin", plugin.getName())
            ));
            e.printStackTrace();
        }
    }

}