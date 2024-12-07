/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) BrycensRanch <https://github.com/BrycensRanch>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author BrycensRanch
 * @author Romvnly
 * @link https://github.com/BrycensRanch/TownyPlus
 */

package me.romvnly.TownyPlus.command.commands;


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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.Source;

import java.util.List;

public final class ReloadCommand extends BaseCommand {
    ComponentLogger logger = TownyPlusMain.plugin.logger;
    public ReloadCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("reload")
                        .permission(Constants.RELOAD_PERMISSION)
                        .handler(this::execute));
    }

    private void execute(final @NonNull CommandContext<CommandSender> context) {
        BukkitRunnable reloadTask = new BukkitRunnable() {
            @Override
            public void run() {
                Audience sender = plugin.adventure().sender(context.sender());
                try {
                    Config.reload();
                    Lang.reload();
                    if (plugin.chatHook != null) {
                        plugin.chatHook.reload();
                    }
                    if (plugin.expansion != null) {
                        Boolean didUnRegisterSuccessfully = plugin.expansion.unregister();
                        if (didUnRegisterSuccessfully) {
                            logger.info("Successfully unregistered with PlaceholderAPI!");
                        }
                        else {
                            logger.warn("Failed to unregister with PlaceholderAPI!");
                        }
                        Boolean didRegisterSuccesfully = plugin.expansion.register();
                        if (didRegisterSuccesfully) {
                            logger.info("Successfully registered with PlaceholderAPI!");
                        }
                        else {
                            logger.warn("Failed to register with PlaceholderAPI!");
                        }
                    }
                    if (plugin.database != null) {
                        plugin.database.reload();
                    }
                    if (plugin.restAPI != null && plugin.restAPI.active) {
                        plugin.restAPI.stopServer();
                    }
                    if (Config.HTTPD_ENABLED) plugin.restAPI.startServer(Config.HTTPD_BIND, Config.HTTPD_PORT);

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
        };
        reloadTask.runTask(plugin);
    }

}