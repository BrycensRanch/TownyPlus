package me.romvnly.TownyPlus.command;

import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import me.romvnly.TownyPlus.command.commands.BypassCommand;
import me.romvnly.TownyPlus.configuration.Lang;
import net.kyori.adventure.text.minimessage.MiniMessage;
import cloud.commandframework.execution.CommandExecutionCoordinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.commands.HelpCommand;
import me.romvnly.TownyPlus.command.exception.CompletedSuccessfullyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CommandManager extends PaperCommandManager<CommandSender> {
    public CommandManager(final @NonNull TownyPlusMain plugin) throws Exception {
        super(
                plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                UnaryOperator.identity(),
                UnaryOperator.identity()
        );
        if (this.queryCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.registerBrigadier();
            final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }
        this.registerExceptionHandlers(plugin);

        ImmutableList.of(
                new HelpCommand(plugin, this),
                new BypassCommand(plugin, this)
//                new ReloadCommand(plugin, this),
//                new TownyBypassCommand(plugin, this),
        ).forEach(BaseCommand::register);

    }

    private void registerExceptionHandlers(final @NonNull TownyPlusMain plugin) {
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(MiniMessage.get().parse("<white>[<gradient:#C028FF:#5B00FF>TownyOverride</gradient>]</white> ")
                                .hoverEvent(MiniMessage.get().parse("Click for help"))
                                .clickEvent(ClickEvent.runCommand(String.format("/%s help", "townyoverride"))))
                        .append(component)
                        .build())
                .apply(this, AudienceProvider.nativeAudience());

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
        List.of("townyplus", "townplus").forEach(entry -> MAIN_COMMAND_ALIASES.add(entry));
        return this.commandBuilder("townyoverride", MAIN_COMMAND_ALIASES.toArray(String[]::new))

                /* MinecraftHelp uses the MinecraftExtrasMetaKeys.DESCRIPTION meta, this is just so we give Bukkit a description
                 * for our commands in the Bukkit and EssentialsX '/help' command */
                .meta(CommandMeta.DESCRIPTION, Lang.BUKKIT_COMMAND_DESCRIPTION);
    }
}
