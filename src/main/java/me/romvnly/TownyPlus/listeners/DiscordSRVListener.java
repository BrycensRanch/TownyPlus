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

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ChannelType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.json.JSONObject;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.model.SavedTownData;
import me.romvnly.TownyPlus.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import com.palmergames.bukkit.towny.TownyAPI;

import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Nonnull;

public class DiscordSRVListener extends ListenerAdapter {
    private TownyPlusMain plugin;
    public DiscordSRVListener(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super();
        this.plugin = plugin;
    }
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordReadyEvent(DiscordReadyEvent event) {
        // Example of using JDA's events
        // We need to wait until DiscordSRV has initialized JDA, thus we're doing this inside DiscordReadyEvent
        var jda = DiscordUtil.getJda();
        jda.addEventListener(this);

        // ... we can also do anything other than listen for events with JDA now,
        plugin.logger.info("Chatting on Discord with " + jda.getUsers().size() + " users!");
        // see https://ci.dv8tion.net/job/JDA/javadoc/ for JDA's javadoc
        // see https://github.com/DV8FromTheWorld/JDA/wiki for JDA's wiki
    }
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onGuildUnavailable(@SuppressWarnings("null") GuildUnavailableEvent event) {
        plugin.logger.warn("Oh no " + event.getGuild().getName() + " went unavailable :(");
    }
    public void assignTownRoles(final MessageReceivedEvent event) {
        var linkedMinecraftAccountUUID =  ((DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV")).getAccountLinkManager().getUuid(event.getAuthor().getId());
        Debug.log(linkedMinecraftAccountUUID.toString());
        if (linkedMinecraftAccountUUID != null && !linkedMinecraftAccountUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                Debug.log("NOT NULL");
                var resident = TownyAPI.getInstance().getResident(linkedMinecraftAccountUUID);
                Debug.log(resident.getFormattedName());
                var town = resident.getTownOrNull();
                Debug.log(town.getFormattedName());
                SavedTownData savedTownData;
                try {
                    savedTownData = plugin.database.findTownByName(town.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    savedTownData = null;
                }
                if (town != null && savedTownData != null) {
                    Debug.log("TOWN DISCORD DATA EXISTS");
                    var discordRolesJson = savedTownData.getTownDiscordRoles();
                    JSONObject discordRoles = new JSONObject(discordRolesJson);
                    var guild = event.getGuild();
                    Debug.log(discordRolesJson);
                    try {
                        String globalTownMayorRoleName = "Town Mayor";
                        String globalTownAssistantRoleName = "Town Assistant";
                        String globalTownResidentRoleName = "Town Resident";
                        guild.addRoleToMember(event.getAuthor().getId(), guild.getRolesByName(globalTownMayorRoleName, false).get(0)).complete();
                        guild.addRoleToMember(event.getAuthor().getId(), guild.getRolesByName(globalTownAssistantRoleName, false).get(0)).complete();
                        guild.addRoleToMember(event.getAuthor().getId(), guild.getRolesByName(globalTownResidentRoleName, false).get(0)).complete();

                    } catch(Exception e) {}
                    for (var rank : discordRoles.keySet()) {
                        Debug.log(rank);
                            String discordRoleId;
                            try {
                                discordRoleId = discordRoles.getString(rank);
                            } catch(Exception e) {
                                e.printStackTrace();
                                discordRoleId = null;
                            }
                            Debug.log(discordRoleId);
                            if (discordRoleId != null) {
                                try {
                                    guild.addRoleToMember(event.getMember(), guild.getRoleById(discordRoleId)).complete();    
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }     
                                     
                        } 
                    }

                    
                }
        }

    }
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onMessageReceived( final MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            Debug.log(Component.text("Author is bot").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            return;
        }
        if (event.getMember() == null) {
        Debug.log(Component.text("Member is null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
        return;
        }
       if (!event.isFromType(ChannelType.TEXT)) {
            Debug.log(Component.text("Not from type text").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            return;
        }
        try {
            assignTownRoles(event);

        } catch(Exception e) {
            e.printStackTrace();
        }
        // This checks for codes and generates appropriate channels and messages
        // It was moved to a new class because it was getting too big
        try {
            plugin.discordSRVChannelCreator.handleDiscordMessageEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
