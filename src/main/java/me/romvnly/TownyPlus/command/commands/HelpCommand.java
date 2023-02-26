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

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.stream.Collectors;

public final class HelpCommand extends BaseCommand {

    private final MinecraftHelp<CommandSender> minecraftHelp;

    public HelpCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
        this.minecraftHelp = new MinecraftHelp(
                String.format("/%s help", "townyplus"),
                sender -> (Audience) plugin.adventure(),
                commandManager
        );
        this.minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(
                TextColor.color(0x816efa),
                NamedTextColor.WHITE,
                TextColor.color(0x2a4858),
                NamedTextColor.GOLD,
                NamedTextColor.DARK_GRAY
        ));
        this.minecraftHelp.setMessage(MinecraftHelp.MESSAGE_HELP_TITLE, "TownyPlus Help");
    }

    @Override
    public void register() {
        final var commandHelpHandler = this.commandManager.createCommandHelpHandler();
        final var helpQueryArgument = StringArgument.<CommandSender>builder("query")
                .greedy()
                .asOptional()
                .withSuggestionsProvider((context, input) -> {
                    final var indexHelpTopic = (CommandHelpHandler.IndexHelpTopic<CommandSender>) commandHelpHandler.queryHelp(context.getSender(), "");
                    return indexHelpTopic.getEntries()
                            .stream()
                            .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                            .collect(Collectors.toList());
                })
                .build();

        this.commandManager.registerSubcommand(builder ->
                builder.literal("help")
                        .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.get().parse("Get help for <pluginName> commands", Template.of("pluginName", plugin.getName())))
                        .argument(helpQueryArgument, CommandUtil.description("Help Query"))
                        .permission(Constants.HELP_PERMISSION)
                        .handler(this::executeHelp));
    }

    private void executeHelp(final @NonNull CommandContext<CommandSender> context) {
        this.minecraftHelp.queryCommands(
                context.<String>getOptional("query").orElse(""),
                context.getSender()
        );
    }

}