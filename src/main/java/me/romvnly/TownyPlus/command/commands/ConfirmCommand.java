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

import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class ConfirmCommand extends BaseCommand {

  public ConfirmCommand(
    final @NonNull TownyPlusMain plugin,
    final @NonNull CommandManager commandManager
  ) {
    super(plugin, commandManager);
  }

  @Override
  public void register() {
    this.commandManager.registerSubcommand(builder ->
        builder
          .literal("confirm")
          .meta(
            MinecraftExtrasMetaKeys.DESCRIPTION,
            MiniMessage.miniMessage().deserialize("Confirm a pending command")
          )
          .permission(Constants.CONFIRM_PERMISSION)
          .handler(
            this.commandManager.confirmationManager.createConfirmationExecutionHandler()
          )
      );
  }
}
