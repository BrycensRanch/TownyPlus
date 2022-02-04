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

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class DiscordSRVListener {
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getGuild() == null) return;
//        if (event.getGuild().getId() != "422627791738109953") return; // testing server
//        if (event.getChannel().getId() != "912487580241645568") return;  // town chat
        UUID linkedUUID = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getAuthor().getId());
        if (linkedUUID == null) {
            event.getChannel().sendMessage("AYO HOMES YOU NOT LINKED TO DISCORDSRV!! LINK NOW!! GO IN GAME TYPE DAT /discord link");
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(linkedUUID);
        // this should never happen but ehhh
        if (!offlinePlayer.hasPlayedBefore()) return;
        final Resident resident = TownyUniverse.getInstance().getResident(linkedUUID);
        Town town = resident.getTownOrNull();
        if (town == null) return;
        final Member member = event.getMember();
        final Role topRole = DiscordUtil.getTopRole(member);
        final TextComponent textComponent = Component.text()
                .content("[" + town.getName() + "]")
                .color(TextColor.color(0x443344))
                .append(Component.text().content("[" + event.getAuthor().getName() + " | " + topRole.getName() + "] ").color(NamedTextColor.LIGHT_PURPLE).build())
                .append(Component.text().content(event.getMessage().getContentStripped()).color(NamedTextColor.AQUA).build())
                .build();
        TownyPlusMain.plugin.chatHook.broadcastMessageToChannel("Town", textComponent);

    }
}
