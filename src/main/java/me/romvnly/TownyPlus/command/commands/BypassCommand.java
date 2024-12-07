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
/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */


import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import me.romvnly.TownyPlus.util.Debug;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.DurationParser.durationParser;


public final class BypassCommand extends BaseCommand {

    final List<String> TIME_DURATIONS = new ArrayList<>();
    final List<String> TOGGLES = new ArrayList<>();

    public BypassCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("bypass")
                        .optional("toggle", stringParser(), DefaultValue.constant("on"), SuggestionProvider.suggestingStrings("on", "off"))
                        .optional("time", durationParser(), DefaultValue.constant(Duration.ofSeconds(30L)))
                        .permission(Constants.BYPASS_PERMISSION)
                        .handler(this::executeBypass));
    }

    public void executeBypass(final @NonNull CommandContext<CommandSender> context) {
        String toggleString = context.getOrDefault("toggle", "on");
        Audience sender = plugin.adventure().sender(context.sender());
        boolean toggled;
        switch (toggleString.toLowerCase()) {
            case "on":
                toggled = true;
                break;
            case "off":
                toggled = false;
                break;
            default:
                sender.sendMessage(MiniMessage.miniMessage().deserialize("Sorry, I didn't get that. Please enter on/off"));
                return;
        }
        Duration time;
        try {
            time = context.get("time");
        } catch (
                NullPointerException e) {
            time= Duration.ofSeconds(30);
            Debug.log("Time was null, setting to 30 seconds");
        }
        if (!toggled) time = null;
        Player target = CommandUtil.resolvePlayer(context, plugin);
        if (toggled) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Toggled <aqua><mode></aqua> <player> Towny bypass mode for</green> <yellow><duration> seconds</yellow><green>.</green>",
                    Placeholder.unparsed("mode", toggled ? "on" : "off"),
                    Placeholder.unparsed("player", target.getName() == context.sender().getName() ? "your" : target.getName() + "'s"),
                    Placeholder.unparsed("duration", String.valueOf(time.getSeconds()))
            ));
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Toggled <aqua><mode></aqua> <player> Towny bypass mode<green>.</green>",
                    Placeholder.unparsed("mode", toggled ? "on" : "off"),
                    Placeholder.unparsed("player", target.getName() == context.sender().getName() ? "your" : target.getName() + "'s")
            ));
        }
        if (target.getName() != context.sender().getName() && toggled) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Your towny bypass mode has been <mode> for <yellow><duration> seconds</yellow></green>",
                    Placeholder.unparsed("mode", toggled ? "enabled" : "disabled"),
                    Placeholder.unparsed("duration", String.valueOf(time.getSeconds()))
            ));
        }
        if (target.getName() != context.sender().getName() && !toggled) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Your towny bypass mode has been <aqua><mode></aqua>, your logs will be uploaded to our Discord Server!</green>",
                    Placeholder.unparsed("mode", toggled ? "enabled" : "disabled")
            ));
            return;
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Everything you do is logged and will be posted to our Discord Server's logs.</red>"));
        if (time == null) return;
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Your towny bypass mode has been disabled.</red>"));
        }, time.getSeconds() * 20L); //20 Tick (1 Second) delay before run() is called
    }

}