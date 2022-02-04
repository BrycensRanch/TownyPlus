/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.util;

import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.exception.CompletedSuccessfullyException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static @NonNull Player resolvePlayer(final @NonNull CommandContext<CommandSender> context, TownyPlusMain plugin) {
        final Audience sender = plugin.adventure().sender(context.getSender());
        final CommandSender rawSender = context.getSender();

        final SinglePlayerSelector selector = context.getOrDefault("player", null);

        if (selector == null) {
            if (rawSender instanceof Player) {
                return (Player) rawSender;
            }
            sender.sendMessage(MiniMessage.get().parse("<red>You must specify a target player when running this command from console"));
            throw new CompletedSuccessfullyException();
        }

        final Player targetPlayer = selector.getPlayer();
        if (targetPlayer == null) {
            sender.sendMessage(MiniMessage.get().parse("<red>No player found for input '<input>'", Template.of("input", selector.getSelector())));
            throw new CompletedSuccessfullyException();
        }

        return targetPlayer;
    }

    public static @NonNull RichDescription description(final @NonNull String miniMessage, @NonNull Template @NonNull ... placeholders) {
        return RichDescription.of(MiniMessage.get().parse(miniMessage, placeholders));
    }
}