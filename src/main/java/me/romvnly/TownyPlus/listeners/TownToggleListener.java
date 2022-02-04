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

import com.palmergames.bukkit.towny.event.town.TownMayorChangeEvent;
import com.palmergames.bukkit.towny.event.town.toggle.TownTogglePVPEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TownToggleListener implements Listener {
    public TownToggleListener() {
        TownyPlusMain.plugin.getServer().getPluginManager().registerEvents(this, TownyPlusMain.plugin);
    }

    public static String humanizeToggle(boolean trueOrFalse) {
        return trueOrFalse ? "on" : "off";
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTownPVPToggled(TownTogglePVPEvent event) {
        // Resident that called it
        Resident resident = event.getResident();
        boolean oldState = event.getCurrentState();
        boolean newState = event.getFutureState();
        Town town = event.getTown();
        TownyPlusMain.plugin.adventure().console().sendMessage(MiniMessage.get().parse(
                "<red><player> has toggled PVP for <town> <newState></red>",
                Template.of("player", event.isAdminAction() ? "An Admin" : resident.getFormattedName()),
                Template.of("newState", humanizeToggle(newState)),
                Template.of("town", town.getFormattedName())
        ));
    }
}
