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
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import me.romvnly.TownyPlus.command.commands.BypassCommand;
import me.romvnly.TownyPlus.command.commands.ReloadCommand;
import me.romvnly.TownyPlus.command.commands.VersionCommand;
import me.romvnly.TownyPlus.configuration.Lang;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import cloud.commandframework.execution.CommandExecutionCoordinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.commands.HelpCommand;
import me.romvnly.TownyPlus.command.exception.CompletedSuccessfullyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CommandManager extends BukkitCommandManager<CommandSender> {
    public CommandManager(final @NonNull TownyPlusMain plugin) throws Exception {
        super(
                plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                UnaryOperator.identity(),
                UnaryOperator.identity()
        );
        if (this.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.registerBrigadier();
            final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }
        this.registerExceptionHandlers(plugin);

        ImmutableList.of(
                new HelpCommand(plugin, this),
                new BypassCommand(plugin, this),
                new ReloadCommand(plugin, this),
                new VersionCommand(plugin, this)
//                new TownyBypassCommand(plugin, this),
        ).forEach(BaseCommand::register);

    }

    private void registerExceptionHandlers(@NonNull TownyPlusMain plugin) {
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(MiniMessage.get().parse("<dark_gray>[<gradient:yellow:gold>TownyPlus</gradient>]</dark_gray> ")
                                .hoverEvent(MiniMessage.get().parse("Click for help"))
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
        final List<String> MAIN_COMMAND_ALIASES = new ArrayList<>();
        MAIN_COMMAND_ALIASES.addAll(List.of("townyplus", "townplus"));
        return this.commandBuilder("townyplus", MAIN_COMMAND_ALIASES.toArray(String[]::new))

                /* MinecraftHelp uses the MinecraftExtrasMetaKeys.DESCRIPTION meta, this is just so we give Bukkit a description
                 * for our commands in the Bukkit and EssentialsX '/help' command */
                .meta(CommandMeta.DESCRIPTION, Lang.BUKKIT_COMMAND_DESCRIPTION);
    }
}
