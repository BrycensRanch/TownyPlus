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
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class BypassCommand extends BaseCommand {

    final List<String> TIME_DURATIONS = new ArrayList<>();
    final List<String> TOGGLES = new ArrayList<>();

    public BypassCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    public static long parseDuration(String text) throws IllegalArgumentException {
        // Given a string like 1w3d4h5m, we will return a millisecond duration
        long result = 0;
        int numIdx = 0;
        for (int i = 0; i < text.length(); i++) {
            char at = text.charAt(i);
            if (at == 'd' || at == 'w' || at == 'm' || at == 'h' || at == 's') {
                String ns = text.substring(numIdx, i);
                numIdx = i + 1;/*www . j  av  a 2 s  .  c  o  m*/

                if (ns.isEmpty()) {
                    continue;
                }

                int n = Integer.parseInt(ns);
                switch (at) {
                    case 'd':
                        result += TimeUnit.DAYS.toMillis(n);
                        break;
                    case 'w':
                        result += TimeUnit.DAYS.toMillis(n * 7);
                        break;
                    case 'h':
                        result += TimeUnit.HOURS.toMillis(n);
                        break;
                    case 'm':
                        result += TimeUnit.MINUTES.toMillis(n);
                        break;
                    case 's':
                        result += TimeUnit.SECONDS.toMillis(n);
                        break;
                }
            } else if (!Character.isDigit(at)) {
                throw new IllegalArgumentException("Character " + at + " in position " + (i + 1) + " not valid");
            }
        }

        return result;
    }

    public static boolean isTimeDuration(String strTime) {
        if (strTime == null) {
            return false;
        }
        try {
            parseDuration(strTime);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public void register() {
        final var toggleArgument = StringArgument.<CommandSender>newBuilder("toggle")
                .asOptional()
                .withSuggestionsProvider((context, input) -> List.of("on", "off"))
                .build();
        final var timeArgument = StringArgument.<CommandSender>newBuilder("time")
                .asOptional()
                .withSuggestionsProvider((context, input) -> {
                    if (context.get("toggle").toString().toLowerCase() == "off") {
                        return List.of();
                    }
                    if (!isTimeDuration(input)) {
                        return List.of();
                    }
                    return List.of(input + "s", input + "m", input + "h", input + "d", input + "w");
                })
                .build();
        this.commandManager.registerSubcommand(builder ->
                builder.literal("bypass").meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.get().parse("Bypass towny's stupid protections!"))
                        .argument(toggleArgument, CommandUtil.description("On/off to force enable/disable"))
                        .argument(timeArgument, CommandUtil.description("Duration to bypass towny's protections on Towns"))
                        .argument(SinglePlayerSelectorArgument.optional("player"), CommandUtil.description("Defaults to the executing player if unspecified (console must specify a player)"))
                        .permission(Constants.BYPASS_PERMISSION)
                        .handler(this::executeBypass));
    }

    private void executeBypass(final @NonNull CommandContext<CommandSender> context) {
        String toggleString = context.getOrDefault("toggle", "on");
        Audience sender = plugin.adventure().sender(context.getSender());
        boolean toggled;
        switch (toggleString.toLowerCase()) {
            case "on" -> toggled = true;
            case "off" -> toggled = false;
            default -> {
                sender.sendMessage(MiniMessage.get().parse("Sorry, I didn't get that. Please enter on/off"));
                return;
            }
        }
        Duration time = null;
        try {
            time = Duration.ofMillis(parseDuration(context.getOrDefault("time", "30s")));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MiniMessage.get().parse("<dark_red>That's not a time duration!</dark_red>"));
            return;
        }
        if (!toggled) time = null;
        Player target = CommandUtil.resolvePlayer(context, plugin);
        if (toggled) {
            sender.sendMessage(MiniMessage.get().parse(
                    "<green>Toggled <aqua><mode></aqua> <player> Towny bypass mode for</green> <yellow><duration> seconds</yellow><green>.</green>",
                    Template.of("mode", toggled ? "on" : "off"),
                    Template.of("player", target.getName() == context.getSender().getName() ? "your" : target.getName() + "'s"),
                    Template.of("duration", String.valueOf(time.getSeconds()))
            ));
        } else {
            sender.sendMessage(MiniMessage.get().parse(
                    "<green>Toggled <aqua><mode></aqua> <player> Towny bypass mode<green>.</green>",
                    Template.of("mode", toggled ? "on" : "off"),
                    Template.of("player", target.getName() == context.getSender().getName() ? "your" : target.getName() + "'s")
            ));
        }
        if (target.getName() != context.getSender().getName() && toggled) {
            sender.sendMessage(MiniMessage.get().parse(
                    "<green>Your towny bypass mode has been <mode> for <yellow><duration> seconds</yellow></green>",
                    Template.of("mode", toggled ? "enabled" : "disabled"),
                    Template.of("duration", String.valueOf(time.getSeconds()))
            ));
        }
        if (target.getName() != context.getSender().getName() && !toggled) {
            sender.sendMessage(MiniMessage.get().parse(
                    "<green>Your towny bypass mode has been <aqua><mode></aqua>, your logs will be uploaded to our Discord Server!</green>",
                    Template.of("mode", toggled ? "enabled" : "disabled")
            ));
        }
        if (toggled) {
            sender.sendMessage(MiniMessage.get().parse("<red>Everything you do is logged and will be posted to our Discord Server's logs.</red>"));
        }
//        else {
//            target.getPlayer().sendMessage(MiniMessage.get().parse("<purple></purple>"));
//        }
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage("This message is shown after a second...");
            }
        }, 20L); //20 Tick (1 Second) delay before run() is called
        return;
    }

}