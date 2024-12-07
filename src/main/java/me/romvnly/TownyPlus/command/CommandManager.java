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

package me.romvnly.TownyPlus.command;

import me.romvnly.TownyPlus.command.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import me.romvnly.TownyPlus.TownyPlusMain;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.processors.cache.SimpleCache;
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;
import org.incendo.cloud.processors.confirmation.ImmutableConfirmationConfiguration;

import static net.kyori.adventure.text.Component.text;

public class CommandManager extends LegacyPaperCommandManager<CommandSender> {
    final static SenderMapper<CommandSender, CommandSender> mapperFunction = SenderMapper.identity();
    //
    // This is a function that will provide a command execution coordinator that parses and executes commands
    // asynchronously
    //
    final static ExecutionCoordinator executionCoordinatorFunction =
            ExecutionCoordinator.asyncCoordinator();

    public ImmutableConfirmationConfiguration confirmationConfiguration;
    public ConfirmationManager<CommandSender> confirmationManager;
    @SuppressWarnings("unchecked")
public CommandManager(final @NonNull TownyPlusMain plugin) throws Exception {
        super(
                plugin,
                executionCoordinatorFunction,
                mapperFunction
        );
        if (this.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.registerBrigadier();
            final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
            brigManager.setNativeNumberSuggestions(true);
        }
        this.registerExceptionHandlers(plugin);
        this.confirmationConfiguration = ConfirmationConfiguration.<CommandSender>builder()
                .cache(SimpleCache.of())
                .noPendingCommandNotifier((sender) -> sender.sendMessage(ChatColor.RED + "You don't have any pending commands."))
                .confirmationRequiredNotifier((sender, senderConfirmationContext) -> sender.sendMessage(ChatColor.RED + "Confirmation required. Confirm by adding confirm to this command."))
                .build();
        this.confirmationManager = ConfirmationManager.confirmationManager(confirmationConfiguration);
        this.registerCommandPostProcessor(
                confirmationManager.createPostprocessor()
        );

        ImmutableList.of(
                new HelpCommand(plugin, this),
                new DumpCommand(plugin, this),
                new DiscordCommand(plugin, this),
                new ConfirmCommand(plugin, this),
                new ChestCommand(plugin, this),
                new BypassCommand(plugin, this),
                new ReloadCommand(plugin, this),
                new VersionCommand(plugin, this)
        ).forEach(BaseCommand::register);

    }

    private void registerExceptionHandlers(@NonNull TownyPlusMain plugin) {

    }

    public void registerSubcommand(UnaryOperator<Command.Builder<CommandSender>> builderModifier) {
        this.command(builderModifier.apply(this.rootBuilder()));
    }

    public Command.@NonNull Builder<CommandSender> rootBuilder() {
        final List<String> MAIN_COMMAND_ALIASES = new ArrayList<>();
        MAIN_COMMAND_ALIASES.addAll(List.of("townyplus", "townplus"));
        return this.commandBuilder("townyplus", MAIN_COMMAND_ALIASES.toArray(String[]::new));

    }
}