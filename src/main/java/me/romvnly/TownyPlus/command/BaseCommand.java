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

import me.romvnly.TownyPlus.TownyPlusMain;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.paper.PaperCommandManager;

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