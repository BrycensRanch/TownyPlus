/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer;
import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.romvnly.TownyPlus.Database;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.model.SavedCode;
import me.romvnly.TownyPlus.model.SavedTownData;
import me.romvnly.TownyPlus.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;



public class DiscordSRVChannelCreator {
    private final TownyPlusMain plugin = TownyPlusMain.getInstance();
    // TODO: Split up into multiple functions for better readability and maintainability
    // public void initializeGuild(@Nonnull final MessageReceivedEvent event, @Nonnull Guild guild) {) {
        

    // }

    public void handleDiscordMessageEvent(@Nonnull final MessageReceivedEvent event) {
        try {
            SavedCode code = plugin.database.findCodeByString(event.getMessage().getContentDisplay());
            if (code == null) {
                Debug.log(Component.text("Code is null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
                return;
            }
            if (code != null) {
                UUID linkedAccountUUID = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getAuthor().getId());
                if (linkedAccountUUID == null || linkedAccountUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                    event.getMessage().reply(":warning: | You must link your discord account to your minecraft account before attempting to give the code. Or maybe, you've stolen the code from the town owner? Shame on you.").queue();
                    return;
                }

                Resident resident = TownyUniverse.getInstance().getResident(UUID.fromString(code.getCreatedBy()));
                Town residentTown = resident.getTownOrNull();
                if (residentTown == null) {
                    event.getMessage().reply(":warning: | You must be in a town to redeem a code.").complete();
                    return;
                }
                // Sanity checks
                if (!residentTown.isMayor(resident)) {
                    event.getMessage().reply(":warning: | Only Town Mayors may redeem code, skrub.").complete();
                    return;
                }
                JDA jda = DiscordUtil.getJda();
                Guild guild = event.getGuild();
                String category = "Towny";
                String logsCate = "Logs";
                if (guild.getCategoriesByName(category, true).size() == 0) {
                    guild.createCategory(category).complete();
                }
                if (guild.getCategoriesByName(logsCate, true).size() == 0) {
                    guild.createCategory(logsCate).complete();
                }
                Category townyCategory = guild.getCategoriesByName(category, true).get(0);
                Category logsCategory = guild.getCategoriesByName(logsCate, true).get(0);

                if (guild.getTextChannelsByName("town-chat", true).size() == 0) {
                    guild.createTextChannel("town-chat", townyCategory).complete();
                }
                TextChannel townChat = guild.getTextChannelsByName("town-chat", true).get(0);
                townChat.createWebhook(plugin.getName()).complete();
                String townChatChannelId = townChat.getId();
                String createdTownChatWebhook = townChat.retrieveWebhooks().complete().get(0).getUrl();

                if (guild.getTextChannelsByName("towny-info", true).size() == 0) {
                    guild.createTextChannel("towny-info", townyCategory).complete();
                }


                TextChannel townyInfo = guild.getTextChannelsByName("towny-info", true).get(0);

                String townyInfoChannelId = townyInfo.getId();
                if (townyInfo.retrieveWebhooks().complete().stream().filter(webhook -> webhook.getName() == plugin.getName()).toList().size() == 0)
                townyInfo.createWebhook(plugin.getName()).complete();
                String createdTownyInfoWebhook = townyInfo.retrieveWebhooks().complete().get(0).getUrl();

                if (guild.getTextChannelsByName("nation-chat", true).size() == 0) {
                    guild.createTextChannel("nation-chat", townyCategory).complete();
                }

                TextChannel nationChat = guild.getTextChannelsByName("nation-chat", true).get(0);

                String nationChatChannelId = nationChat.getId();
                if (nationChat.retrieveWebhooks().complete().stream().filter(webhook -> webhook.getName() == plugin.getName()).toList().size() == 0)
                    nationChat.createWebhook(plugin.getName()).complete();
                String createdNationChatWebhook = nationChat.retrieveWebhooks().complete().get(0).getUrl();

                if (guild.getTextChannelsByName("ally-chat", true).size() == 0) {
                    guild.createTextChannel("ally-chat", townyCategory).complete();
                }
                TextChannel allyChat = guild.getTextChannelsByName("ally-chat", true).get(0);
                String allyChatChannelId = allyChat.getId();
                if (allyChat.retrieveWebhooks().complete().stream().filter(webhook -> webhook.getName() == plugin.getName()).toList().size() == 0) allyChat.createWebhook(plugin.getName()).complete();
                String createdAllyChatWebhook = allyChat.retrieveWebhooks().complete().get(0).getUrl();

                if (guild.getTextChannelsByName("towny-logs", true).size() == 0)
                guild.createTextChannel("towny-logs", logsCategory).complete();

                TextChannel townyLogs = guild.getTextChannelsByName("towny-logs", true).get(0);
                if (townyLogs.retrieveWebhooks().complete().stream().filter(webhook -> webhook.getName() == plugin.getName()).toList().size() == 0) townyLogs.createWebhook(plugin.getName()).complete();
                String townyLogsChannelId = townyLogs.getId();
                String createdTownyLogsWebhook = townyLogs.retrieveWebhooks().complete().get(0).getUrl();

                event.getMessage().reply(String.format(":white_check_mark:  | Created channels for town %s", residentTown.getFormattedName())).complete();
                event.getMessage().reply("From now on, all town chat in MC or Discord will be sent to their respective channels.").complete();
                plugin.database.deleteCode(code);
                plugin.logger.info(Lang.parse("<user> Redeemed code for <town_formatted> <code>", Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("town_formatted", residentTown.getFormattedName()), Placeholder.unparsed("code", code.getCode())));
                Message msg = townyInfo.sendMessage(new EmbedBuilder().addField("Members", residentTown.getResidents().stream().map((res) -> res.getFormattedName()).toString(), true).setDescription(String.format("Towny Info channel for %s", residentTown.getFormattedName())).build()).complete();
                String mayorRoleName = residentTown.getName() + " | Mayor";
                String assistantRoleName = residentTown.getName() + " | Assistant";
                String residentRoleName = residentTown.getName() + " | Member";
               if (guild.getRolesByName(mayorRoleName, true).size() == 0)  guild.createRole().setName(mayorRoleName).complete();
                if (guild.getRolesByName(assistantRoleName, true).size() == 0) guild.createRole().setName(assistantRoleName).complete();
                if (guild.getRolesByName(residentRoleName, true).size() == 0)  guild.createRole().setName(residentRoleName).complete();
                Role mayorRole = guild.getRolesByName(mayorRoleName, true).get(0);
                Role assistantRole = guild.getRolesByName(assistantRoleName, true).get(0);
                Role residentRole = guild.getRolesByName(residentRoleName, true).get(0);
                ObjectNode roles = TownyPlusMain.JSONMapper.createObjectNode();
                roles.put("mayor", mayorRole.getId());
                roles.put("assistant", assistantRole.getId());
                roles.put("resident", residentRole.getId());
                String rolesString = TownyPlusMain.JSONMapper.writeValueAsString(roles);
                try {
                    plugin.database.createTownData(new SavedTownData(residentTown.getName(), guild.getId(), townChatChannelId, createdTownChatWebhook, nationChatChannelId, townyLogsChannelId, createdTownyLogsWebhook, townyInfoChannelId, createdTownyInfoWebhook, msg.getId(), rolesString));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        SavedTownData savedTownData;
        try {
            savedTownData = plugin.database.findTownByDiscordServerId(event.getGuild().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if (savedTownData == null) {
            Debug.log("No saved data found for this discord server");
            return;
        }
        Town town = TownyAPI.getInstance().getTown(savedTownData.getName());
        if (town == null) {
            Debug.log("No Towny town found for this discord server");
            return;
        }
        final Member member = event.getMember();
        final Role topRole = DiscordUtil.getTopRole(member);
        Component discordMessageComponent = MinecraftSerializer.INSTANCE.serialize(event.getMessage().getContentDisplay());
        final TextComponent textComponent = Component.text()
                .content("[" + town.getName() + " Discord"+ "] ").color(TextColor.fromHexString(town.getMapColorHexCode()))
                .append(Component.text().content("[" + event.getAuthor().getName() + " | " + topRole.getName() + "]: ").color(NamedTextColor.RED).build())
                .append(discordMessageComponent)
                .hoverEvent(Component.text().content("This message came from "+ town.getName() + "'s Discord Server" ).color(NamedTextColor.GRAY).build()).build();

        String channelName = event.getChannel().getName().substring("-chat".length());
        if (
                (channelName.contains("town") && savedTownData.getTownChatDiscordID().equalsIgnoreCase(event.getChannel().getId()))
                        ||
                        (channelName.contains("nation") && savedTownData.getNationChatDiscordID().equalsIgnoreCase(event.getChannel().getId()))
        )  {
            TownyPlusMain.plugin.chatHook.broadcastMessageToChannel(channelName, textComponent, town);
        }
        
    }
}