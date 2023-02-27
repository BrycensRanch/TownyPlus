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

import com.palmergames.bukkit.towny.event.town.TownKickEvent;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KickedFromTownListener implements Listener {
    public KickedFromTownListener() {
        TownyPlusMain.plugin.getServer().getPluginManager().registerEvents(this, TownyPlusMain.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKickedFromTown(TownKickEvent event) {
        Resident kickedResident = event.getKickedResident();
        // Gets whoever kicked the resident. Can either be an instance of Player or CommandSender.
        CommandSender kicker = (CommandSender) event.getKicker();
        Town town = event.getTown();
        TownyPlusMain.plugin.adventure().console().sendMessage(MiniMessage.miniMessage().deserialize(
                "<red><kicked> was kicked from <town> by <kicker>!</red>",
                Placeholder.unparsed("kicked", kickedResident.getName()),
                Placeholder.unparsed("town", town.getFormattedName()),
                Placeholder.unparsed("kicker", kicker.getName())
        ));
    }
}
