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

import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.town.toggle.TownTogglePVPEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TownDeletionListener implements Listener {
    public TownDeletionListener() {
        TownyPlusMain.plugin.getServer().getPluginManager().registerEvents(this, TownyPlusMain.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTownDeletion(DeleteTownEvent event) throws SQLException {
        var nameofDeletedTown = event.getTownName();
        var plugin = TownyPlusMain.plugin;
        var db = plugin.database;
        var townRecord = db.findTownByName(nameofDeletedTown);
        if (townRecord == null) return;
        db.deleteTownData(townRecord.getName());
        plugin.adventure().console().sendMessage(MiniMessage.miniMessage().deserialize(
            "<red>The town <town> has been deleted. It's database entry has been too.",
            Placeholder.unparsed("town", nameofDeletedTown)
    ));
    }

}
