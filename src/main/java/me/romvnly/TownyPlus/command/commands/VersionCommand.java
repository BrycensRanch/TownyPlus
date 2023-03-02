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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
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

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class VersionCommand extends BaseCommand {

  public VersionCommand(
    final @NonNull TownyPlusMain plugin,
    final @NonNull CommandManager commandManager
  ) {
    super(plugin, commandManager);
  }

  @Override
  public void register() {
    this.commandManager.registerSubcommand(builder ->
        builder
          .literal("version")
          .meta(
            MinecraftExtrasMetaKeys.DESCRIPTION,
            MiniMessage.miniMessage().deserialize("Check the plugin's version")
          )
          .permission(Constants.VERSION_PERMISSION)
          .handler(this::execute)
      );
  }

  private void execute(final @NonNull CommandContext<CommandSender> context) {
    Audience sender = plugin.adventure().sender(context.getSender());
    try (
      InputStream stream = getClass()
        .getClassLoader()
        .getResourceAsStream("git.properties")
    ) {
      Properties gitProp = new Properties();
      gitProp.load(stream);
      sender.sendMessage(
        MiniMessage
          .miniMessage()
          .deserialize(
            "<rainbow>This server is running <pluginName> version <pluginVersion> (git-<gitBranch>-<gitCommitShort>)</rainbow>",
            Placeholder.unparsed("pluginName", plugin.getName()),
            Placeholder.unparsed(
              "pluginVersion",
              plugin.getDescription().getVersion()
            ),
            Placeholder.unparsed(
              "gitBranch",
              gitProp.getProperty("git.branch")
            ),
            Placeholder.unparsed(
              "gitCommitShort",
              gitProp.getProperty("git.commit.id.abbrev")
            )
          )
          .clickEvent(
            ClickEvent.openUrl(gitProp.getProperty("git.remote.origin.url"))
          )
      );
      plugin.updateChecker.checkNow(context.getSender());
    } catch (
      IOException
      | AssertionError
      | NumberFormatException
      | NullPointerException e
    ) {
      sender.sendMessage(
        MiniMessage
          .miniMessage()
          .deserialize(
            "<red>Could not check plugin version. Check your console.</red>"
          )
      );
      e.printStackTrace();
    }
  }
}
