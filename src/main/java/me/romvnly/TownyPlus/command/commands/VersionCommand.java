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


import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class VersionCommand extends BaseCommand {

    public VersionCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("version").meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize("Check the plugin's version"))
                        .permission(Constants.VERSION_PERMISSION)
                        .handler(this::execute));
    }

    private void execute(final @NonNull CommandContext<CommandSender> context) {
        Audience sender = plugin.adventure().sender(context.getSender());
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("git.properties")) {
            Properties gitProp = new Properties();
            gitProp.load(stream);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<rainbow>This server is running <plugin> version <version> (git-<branch>-<commit>)</rainbow>",
                    // The plugin.yml is considered the source of truth for the plugin name and version, regardless of what gitProperties says.
                    // This should prevent issues when gitProperties is not present (e.g. when building from source with no .git folder).
                    Placeholder.unparsed("plugin", plugin.getDescription().getName()),
                    Placeholder.unparsed("version", plugin.getDescription().getVersion()),
                    Placeholder.unparsed("branch", gitProp.getProperty("git.branch")),
                    Placeholder.unparsed("commit", gitProp.getProperty("git.commit.id.abbrev"))
                    ).clickEvent(ClickEvent.openUrl(gitProp.getProperty("git.remote.origin.url")))
            );
            plugin.updateChecker.checkNow(context.getSender());
        } catch (IOException | AssertionError | NumberFormatException | NullPointerException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Could not check plugin version. Check your console.</red>"
            ));
            e.printStackTrace();
        }
    }

}