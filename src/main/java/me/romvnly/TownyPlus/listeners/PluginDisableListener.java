/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.listeners;

import me.romvnly.TownyPlus.TownyPlusMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PluginDisableListener implements Listener {
    public PluginDisableListener() {
        TownyPlusMain.plugin.getServer().getPluginManager().registerEvents(this, TownyPlusMain.plugin);
    }
    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    public void onPluginDisable(org.bukkit.event.server.PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("Towny")) {
            // Towny has been disabled, disable TownyPlus
            TownyPlusMain.getInstance().getServer().getPluginManager().disablePlugin(TownyPlusMain.getInstance());
        }
        if (event.getPlugin().getName().equals("DiscordSRV")) {
            TownyPlusMain.getInstance().discordSRVListener = null;
        }
    }
}
