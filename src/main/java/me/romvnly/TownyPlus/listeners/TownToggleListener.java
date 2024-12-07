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

package me.romvnly.TownyPlus.listeners;

import com.palmergames.bukkit.towny.event.town.toggle.TownTogglePVPEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Webhook;
import github.scarsz.discordsrv.dependencies.jda.internal.entities.TextChannelImpl;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.util.Debug;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

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
    public void onTownPVPToggled(TownTogglePVPEvent event) throws Exception {
        // Resident that called it
        Resident resident = event.getResident();
//        boolean oldState = event.getCurrentState();
        boolean newState = event.getFutureState();
        Town town = event.getTown();
        var db = TownyPlusMain.plugin.database;
        var townDBRecord = db.findTownByName(town.getName()); 
        if (townDBRecord == null) return; 
        var discordTownyLogChannel = DiscordUtil.getJda().getTextChannelById(townDBRecord.getTownyLogChannelDiscordID());
        if (discordTownyLogChannel == null) {
            Debug.log("discordTownyLogChannel is null for event TownTogglePVPEvent");
            return; 
        }
        discordTownyLogChannel.sendMessage(String.format("%s has toggled PVP for %s %s", event.isAdminAction() ? "An Admin" : resident.getFormattedName(), town.getFormattedName(), humanizeToggle(newState)));
    }
}
