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
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.stream.Collectors;

import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public final class HelpCommand extends BaseCommand {
    public  MinecraftHelp<CommandSender> help;
    public HelpCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);

        this.help = MinecraftHelp.<CommandSender>builder()
                .commandManager(commandManager)
                .audienceProvider(sender -> plugin.adventure().sender(sender))
                .commandPrefix(String.format("/%s help", "townyplus"))
                .colors(MinecraftHelp.helpColors(                TextColor.color(0x816efa),
                        NamedTextColor.WHITE,
                        TextColor.color(0x2a4858),
                        NamedTextColor.GOLD,
                        NamedTextColor.DARK_GRAY
                ))
                .messages(MinecraftHelp.MESSAGE_HELP_TITLE, "TownyPlus Help")
                .build();
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder -> {
            return builder.literal("help")
                    .permission(Constants.HELP_PERMISSION)
                    .handler(this::executeHelp)
                    .optional("query", greedyStringParser(), DefaultValue.constant(""),  SuggestionProvider.blocking((ctx, in) -> commandManager.createHelpHandler()
                            .queryRootIndex(ctx.sender())
                            .entries()
                            .stream()
                            .map(CommandEntry::syntax)
                            .map(Suggestion::suggestion)
                            .collect(Collectors.toList())
                    ));
        });
    }

    private void executeHelp(final @NonNull CommandContext<CommandSender> context) {
        help.queryCommands(context.get("query"), context.sender());
    }

}