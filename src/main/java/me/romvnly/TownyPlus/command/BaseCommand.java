package me.romvnly.TownyPlus.command;

import me.romvnly.TownyPlus.TownyPlusMain;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class BaseCommand {
    protected final TownyPlusMain plugin;
    protected final CommandManager commandManager;

    protected BaseCommand(
            final @NonNull TownyPlusMain plugin,
            final @NonNull CommandManager commandManager
    ) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public abstract void register();
}