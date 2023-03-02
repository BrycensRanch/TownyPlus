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
import java.util.List;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ReloadCommand extends BaseCommand {

  public ReloadCommand(
    final @NonNull TownyPlusMain plugin,
    final @NonNull CommandManager commandManager
  ) {
    super(plugin, commandManager);
  }

  @Override
  public void register() {
    this.commandManager.registerSubcommand(builder ->
        builder
          .literal("reload")
          .meta(
            MinecraftExtrasMetaKeys.DESCRIPTION,
            MiniMessage
              .miniMessage()
              .deserialize("Reload the plugin's configuration")
          )
          .permission(Constants.RELOAD_PERMISSION)
          .handler(this::execute)
      );
  }

  private void execute(final @NonNull CommandContext<CommandSender> context) {
    Audience sender = plugin.adventure().sender(context.getSender());
    try {
      Config.reload();
      Lang.reload();
      sender.sendMessage(
        MiniMessage
          .miniMessage()
          .deserialize(
            "<rainbow><pluginName> has successfully reloaded!</rainbow>",
            Placeholder.unparsed("pluginName", plugin.getName())
          )
      );
      this.plugin.chatHook.reload();
    } catch (Exception e) {
      sender.sendMessage(
        MiniMessage
          .miniMessage()
          .deserialize(
            "<red>Whilst attempting to reload the configuration, the plugin ran into errors. Check your console.</red>"
          )
      );
    }
  }
}
