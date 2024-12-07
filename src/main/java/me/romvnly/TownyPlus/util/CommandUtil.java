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

package me.romvnly.TownyPlus.util;

import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.exception.CompletedSuccessfullyException;
import me.romvnly.TownyPlus.configuration.Lang;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.data.SinglePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.RichDescription;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static @NonNull Player resolvePlayer(final @NonNull CommandContext<CommandSender> context, TownyPlusMain plugin) {
        final Audience sender = plugin.adventure().sender(context.sender());
        final CommandSender rawSender = context.sender();

        final SinglePlayerSelector selector = context.getOrDefault("player", null);

        if (selector == null) {
            if (rawSender instanceof Player) {
                return (Player) rawSender;
            }
            Lang.send(sender, Lang.CONSOLE_MUST_SPECIFY_PLAYER);
            throw new CompletedSuccessfullyException();
        }

        final Player targetPlayer = selector.single().getPlayer();
        if (targetPlayer == null) {
            Lang.send(sender, Lang.parse(Lang.PLAYER_NOT_FOUND_FOR_INPUT, Placeholder.unparsed("input", selector.inputString())));
            throw new CompletedSuccessfullyException();
        }

        return targetPlayer;
    }

    public static @NonNull RichDescription description(final @NonNull String miniMessage) {
        return RichDescription.of(Lang.parse(miniMessage));
    }
}