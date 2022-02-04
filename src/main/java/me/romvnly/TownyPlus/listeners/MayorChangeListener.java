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
import com.palmergames.bukkit.towny.event.town.TownMayorChangeEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MayorChangeListener implements Listener {
    public MayorChangeListener() {
        TownyPlusMain.plugin.getServer().getPluginManager().registerEvents(this, TownyPlusMain.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMayorChange(TownMayorChangeEvent event) {
        Resident oldMayor = event.getOldMayor();
        Resident newMayor = event.getNewMayor();
        Town town = event.getTown();
        TownyPlusMain.plugin.adventure().console().sendMessage(MiniMessage.get().parse(
                "<red><newMayor> now replaces <oldMayor> as mayor of <town></red>",
                Template.of("newMayor", newMayor.getFormattedName()),
                Template.of("oldMayor", newMayor.getFormattedName()),
                Template.of("town", town.getFormattedName())
        ));
    }
}
