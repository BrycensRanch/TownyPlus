package me.romvnly.TownyPlus.util;

import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import me.romvnly.TownyPlus.command.exception.CompletedSuccessfullyException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static @NonNull Player resolvePlayer(final @NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final SinglePlayerSelector selector = context.getOrDefault("player", null);

        if (selector == null) {
            if (sender instanceof Player) {
                return (Player) sender;
            }
            context.getSender().sendMessage(MiniMessage.get().parse("<red>You must specify a target player when running this command from console"));
            throw new CompletedSuccessfullyException();
        }

        final Player targetPlayer = selector.getPlayer();
        if (targetPlayer == null) {
            context.getSender().sendMessage(MiniMessage.get().parse("<red>No player found for input '<input>'", Template.of("input", selector.getSelector())));
            throw new CompletedSuccessfullyException();
        }

        return targetPlayer;
    }

    public static @NonNull RichDescription description(final @NonNull String miniMessage, @NonNull Template @NonNull ... placeholders) {
        return RichDescription.of(MiniMessage.get().parse(miniMessage, placeholders));
    }
}