/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandTree;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import me.romvnly.TownyPlus.command.commands.*;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import net.kyori.adventure.text.minimessage.MiniMessage;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.exception.CompletedSuccessfullyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CommandManager extends PaperCommandManager<CommandSender> {
    final static Function<CommandSender, CommandSender> mapperFunction = Function.identity();
            //
        // This is a function that will provide a command execution coordinator that parses and executes commands
        // asynchronously
        //
        final static Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
                public CommandConfirmationManager<CommandSender> confirmationManager;
                public AnnotationParser<CommandSender> annotationParser;
    public CommandManager(final @NonNull TownyPlusMain plugin) throws Exception {
        super(
                plugin,
                executionCoordinatorFunction,
                mapperFunction,
                mapperFunction
        );
        if (this.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.registerBrigadier();
            final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }
        if (this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.registerAsynchronousCompletions();
        }
        this.registerExceptionHandlers(plugin);
        this.confirmationManager = new CommandConfirmationManager<>(
            /* Timeout */ 30L,
            /* Timeout unit */ TimeUnit.SECONDS,
            /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(
            ChatColor.RED + "Confirmation required. Confirm by adding confirm to this command."),
            /* Action when no confirmation is pending */ sender -> sender.sendMessage(
            ChatColor.RED + "You don't have any pending commands.")
    );
        //
        // Create the annotation parser. This allows you to define commands using methods annotated with
        // @CommandMethod
        //
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        // This will allow you to decorate commands with descriptions
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        this.annotationParser = new AnnotationParser<>(
                /* Manager */ this,
                /* Command sender type */ CommandSender.class,
                /* Mapper for command meta instances */ commandMetaFunction
        );
    //
    // Register the confirmation processor. This will enable confirmations for commands that require it
    //
    this.confirmationManager.registerConfirmationProcessor(this);


    // I'm so close to using reflections to register all the commands...
        // This is so annoying...
        ImmutableList.of(
                new HelpCommand(plugin, this),
                new BypassCommand(plugin, this),
                new ReloadCommand(plugin, this),
                new VersionCommand(plugin, this),
                new ConfirmCommand(plugin, this),
                new DumpCommand(plugin, this),
                new DiscordCommand(plugin, this),
                new ChestCommand(plugin, this)
//                new TownyBypassCommand(plugin, this),
        ).forEach(BaseCommand::register);

    }

    private void registerExceptionHandlers(@NonNull TownyPlusMain plugin) {
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(MiniMessage.miniMessage().deserialize("<dark_gray>[<gradient:yellow:gold>TownyPlus</gradient>]</dark_gray> ")
                                .hoverEvent(MiniMessage.miniMessage().deserialize("Click for help"))
                                .clickEvent(ClickEvent.runCommand(String.format("/%s help", "townyplus"))))
                        .append(component)
                        .build())
                .apply(this, sender -> plugin.adventure().sender(sender));

        final var minecraftExtrasDefaultHandler = Objects.requireNonNull(this.getExceptionHandler(CommandExecutionException.class));
        this.registerExceptionHandler(CommandExecutionException.class, (sender, exception) -> {
            final Throwable cause = exception.getCause();

            if (cause instanceof CompletedSuccessfullyException) {
                return;
            }
            minecraftExtrasDefaultHandler.accept(sender, exception);
        });
    }

    public void registerSubcommand(UnaryOperator<Command.Builder<CommandSender>> builderModifier) {
        this.command(builderModifier.apply(this.rootBuilder()));
    }

    private Command.@NonNull Builder<CommandSender> rootBuilder() {
                //
        // Parse all @CommandMethod-annotated methods
        //
        this.annotationParser.parse(this);
        // Parse all @CommandContainer-annotated classes
        try {
            this.annotationParser.parseContainers();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return this.commandBuilder(Config.MAIN_COMMAND_LABEL, Config.MAIN_COMMAND_ALIASES.toArray(String[]::new))

                /* MinecraftHelp uses the MinecraftExtrasMetaKeys.DESCRIPTION meta, this is just so we give Bukkit a description
                 * for our commands in the Bukkit and EssentialsX '/help' command */
                .meta(CommandMeta.DESCRIPTION, Lang.BUKKIT_COMMAND_DESCRIPTION);
    }
}
