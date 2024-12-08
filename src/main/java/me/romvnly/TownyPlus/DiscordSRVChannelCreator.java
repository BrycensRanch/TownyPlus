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

package me.romvnly.TownyPlus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.model.SavedCode;
import me.romvnly.TownyPlus.model.SavedTownData;
import me.romvnly.TownyPlus.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import javax.annotation.Nonnull;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiscordSRVChannelCreator {
    private final TownyPlusMain plugin = TownyPlusMain.getInstance();

    public void createWebhookIfNotExist(@Nonnull final TextChannel channel, @Nonnull String webhookName) {
        if (channel.retrieveWebhooks().complete().stream().filter(webhook -> webhook.getName().equalsIgnoreCase(plugin.getName())).toList().size() == 0) {
            channel.createWebhook(plugin.getName()).complete();
            Debug.log("Created webhook " + webhookName + " for discord channel " + channel.getName());
        }
    }
    public void createCategoryIfNotExist(@Nonnull final Guild guild, @Nonnull String categoryName, @Nonnull final Town town) {
        if (guild.getCategoriesByName(categoryName, true).size() != 0) return;
        var everyoneRole = guild.getPublicRole();

        var category = guild.createCategory(categoryName).addRolePermissionOverride(everyoneRole.getIdLong(), null, Collections.singleton(Permission.VIEW_CHANNEL));
        category.complete();

                for (Role role : guild.getRoles()) {
                    // Check if the role name contains the categoryName 
                    if (role.getName().contains(town.getName()) || role.getName().contains(categoryName)) {
                        // Grant VIEW_CHANNEL permission to the role
                        category.addRolePermissionOverride(role.getIdLong(), 
                                Collections.singleton(Permission.VIEW_CHANNEL), 
                                null).complete(); 
                    }
                }


        Debug.log("Created category " + categoryName + " for discord guild " + guild.getName());
    }
    public void createChannelIfNotExistInCategory(@Nonnull final Category category, @Nonnull String channelName) {
        if (!category.getTextChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(channelName)).toList().isEmpty()) return;
        category.createTextChannel(channelName).complete();
        Debug.log("Created channel " + channelName + " for discord category " + category.getName());
        
    }
    public void createRoleIfNotExist(@Nonnull final Guild guild, @Nonnull String roleName) {
        if (!guild.getRolesByName(roleName, true).isEmpty()) return;
        guild.createRole().setName(roleName).complete();
        Debug.log("Created role " + roleName + " for discord guild " + guild.getName());
    }
    public void listenForLinkedTownDiscordMessages(@Nonnull final MessageReceivedEvent event) {
        var guild = event.getGuild();
        SavedTownData savedTownData;
        try {
            savedTownData = plugin.database.findTownByDiscordServerId(guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if (savedTownData == null) {
            Debug.log("No saved data found for this discord server");
            return;
        }
        var townyAPI = TownyAPI.getInstance();
        Town town = townyAPI.getTown(savedTownData.getName());
        if (town == null) {
            Debug.log("No Towny town found for this discord server");
            return;
        }
        var nation = town.getNationOrNull();
        final Member member = event.getMember();
        if (member == null) {
            Debug.log("No member found for this discord server");
            return;
        }
        Debug.log("stage 4");
        final Role topRole = !member.getRoles().isEmpty() ? member.getRoles().get(0) : null;
        Component discordMessageComponent = MinecraftSerializer.INSTANCE.serialize(event.getMessage().getContentStripped());
        final TextComponent textComponent = Component.text()
                .content("[" + town.getName() + " Discord" + "] ").color(NamedTextColor.GOLD)
                .append(Component.text().content("[" + event.getAuthor().getName() + topRole != null ? " | " + topRole.getName() + "]: " : "").color(NamedTextColor.RED).build())
                .append(discordMessageComponent)
                .hoverEvent(Component.text().content("This message came from " + town.getName() + "'s Discord Server").color(NamedTextColor.GRAY).build()).build();
        var channel = event.getChannel();
        var townDiscordChatId = savedTownData.getTownChatDiscordID();
        var townNationChatId = savedTownData.getNationChatDiscordID();
        if (townDiscordChatId.equals(channel.getId())) {
            TownyPlusMain.plugin.chatHook.broadcastMessageToChannel("town", textComponent, town);
            return;
        } else if (townNationChatId.equals(channel.getId())) {
            TownyPlusMain.plugin.chatHook.broadcastMessageToChannel("nation", textComponent, town);
        } else return;
    }

    public void handleDiscordMessageEvent(@Nonnull final MessageReceivedEvent event) throws Exception {
            listenForLinkedTownDiscordMessages(event);
            SavedCode code = plugin.database.findCodeByString(event.getMessage().getContentDisplay());
            if (code == null) {
                Debug.log(Lang.parse("<red>Code is null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
                return;
            }

            Debug.log(Lang.parse("<red>Code is <bold>not</bold> null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            UUID linkedAccountUUID = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getAuthor().getId());
            if (linkedAccountUUID == null || linkedAccountUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                event.getMessage().reply(":warning: | You must link your discord account to your minecraft account before attempting to give the code. (/discord link in-game) Or maybe, you've stolen the code from the town owner? Shame on you.").queue();
                return;
            }

            Resident resident = TownyUniverse.getInstance().getResident(UUID.fromString(code.getCreatedBy()));
            assert resident != null;
            Town residentTown = resident.getTownOrNull();
            // Sanity checks
            if (residentTown == null) {
                event.getMessage().reply(":warning: | You must be in a town to redeem a code.").complete();
                return;
            }
            if (!residentTown.isMayor(resident)) {
                event.getMessage().reply(":warning: | Only Town Mayors may redeem code, skrub.").complete();
                return;
            }
            Guild guild = Config.MAIN_DISCORD_CREATE_ROLES ? event.getJDA().getGuildById(Config.MAIN_DISCORD_SERVER_ID): event.getGuild();
            String category = Config.MAIN_DISCORD_CREATE_ROLES ? residentTown.getName() : "Towny";
            String logsCate = Config.MAIN_DISCORD_CREATE_ROLES ? category : "Logs";

            // Create channels, categories, and webhooks if they don't exist

            createCategoryIfNotExist(guild, category, residentTown);
            createCategoryIfNotExist(guild, logsCate, residentTown);

            // Retrieving data
            Category townyCategory = guild.getCategoriesByName(category, true).get(0);
            Category logsCategory = guild.getCategoriesByName(logsCate, true).get(0);

            createChannelIfNotExistInCategory(townyCategory, "town-chat");
            TextChannel townChat = guild.getTextChannelsByName("town-chat", true).get(0);
            createWebhookIfNotExist(townChat, plugin.getName());
            String townChatChannelId = townChat.getId();
            String townChatWebhook = townChat.retrieveWebhooks().complete().get(0).getUrl();

            createChannelIfNotExistInCategory(townyCategory, "towny-info");

            TextChannel townyInfo = guild.getTextChannelsByName("towny-info", true).get(0);

            String townyInfoChannelId = townyInfo.getId();
            createWebhookIfNotExist(townyInfo, plugin.getName());
            String townyInfoWebhook = townyInfo.retrieveWebhooks().complete().get(0).getUrl();

            createChannelIfNotExistInCategory(townyCategory, "nation-chat");

            TextChannel nationChat = guild.getTextChannelsByName("nation-chat", true).get(0);

            String nationChatChannelId = nationChat.getId();
            createWebhookIfNotExist(nationChat, plugin.getName());
            String nationChatWebhook = nationChat.retrieveWebhooks().complete().get(0).getUrl();

            createChannelIfNotExistInCategory(townyCategory, "ally-chat");
            TextChannel allyChat = guild.getTextChannelsByName("ally-chat", true).get(0);
            String allyChatChannelId = allyChat.getId();
            createWebhookIfNotExist(allyChat, plugin.getName());
            String allyChatWebhook = allyChat.retrieveWebhooks().complete().get(0).getUrl();

            createChannelIfNotExistInCategory(logsCategory, "towny-logs");
            TextChannel townyLogs = guild.getTextChannelsByName("towny-logs", true).get(0);
            createWebhookIfNotExist(townyLogs, plugin.getName());

            String townyLogsChannelId = townyLogs.getId();
            String townyLogsWebhook = townyLogs.retrieveWebhooks().complete().get(0).getUrl();

            event.getMessage().reply(String.format(":white_check_mark:  | Created channels for town %s\n\n" + "From now on, all town chat in MC or Discord will be sent to their respective channels.", residentTown.getFormattedName())).complete();
            plugin.database.deleteCode(code);
            // Just because the code is redeemed does NOT mean the player who created it is available and can be messaged to begin with.
            var codeCreator = plugin.adventure().player(UUID.fromString(code.getCreatedBy()));
            var offlineCodeCreator = plugin.getServer().getOfflinePlayer(UUID.fromString(code.getCreatedBy()));
            try {
                codeCreator.sendMessage(Lang.parse(Lang.TOWN_DISCORD_LINK_SUCCESS, Placeholder.unparsed("server", guild.getName())));
            } catch(Exception e) {}

            plugin.logger.info(Lang.parse("<user> (<discorduser>) Redeemed code for <town_formatted> <code>. It is linked to <server>", Placeholder.unparsed("user", offlineCodeCreator.getName()), Placeholder.unparsed("town_formatted", residentTown.getFormattedName()), Placeholder.unparsed("code", code.getCode()), Placeholder.unparsed("server", guild.getName()), Placeholder.unparsed("discorduser", event.getAuthor().getName())));
            var residentList = residentTown.getResidents();
            var formattedResidentList = String.join(", ", residentList.stream().map(Resident::getFormattedName).collect(Collectors.toList()));
            Message msg = townyInfo.sendMessageEmbeds(new EmbedBuilder().addField("Members", formattedResidentList, true).setDescription(String.format("Towny Info channel for %s", residentTown.getFormattedName())).build()).complete();
            String mayorRoleName = residentTown.getName() + " | Mayor";
            String assistantRoleName = residentTown.getName() + " | Assistant";
            String residentRoleName = residentTown.getName() + " | Member";
            createRoleIfNotExist(guild, mayorRoleName);
            createRoleIfNotExist(guild, assistantRoleName);
            createRoleIfNotExist(guild, residentRoleName);
            if (Config.MAIN_DISCORD_CREATE_ROLES) {
                String globalTownMayorRoleName = "Town Mayor";
                String globalTownAssistantRoleName = "Town Assistant";
                String globalTownResidentRoleName = "Town Resident";
                createRoleIfNotExist(guild, globalTownMayorRoleName);
                createRoleIfNotExist(guild, globalTownAssistantRoleName);
                createRoleIfNotExist(guild, globalTownResidentRoleName);
            }
            Role mayorRole = guild.getRolesByName(mayorRoleName, true).get(0);
            Role assistantRole = guild.getRolesByName(assistantRoleName, true).get(0);
            Role residentRole = guild.getRolesByName(residentRoleName, true).get(0);
            ObjectMapper JSONMapper = new ObjectMapper()
                .enable(JsonParser.Feature.IGNORE_UNDEFINED)
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
            ObjectNode roles = JSONMapper.createObjectNode();
            roles.put("mayor", mayorRole.getId());
            roles.put("assistant", assistantRole.getId());
            roles.put("resident", residentRole.getId());
            String rolesString = JSONMapper.writeValueAsString(roles);
            Debug.log("Roles:\n\n" + rolesString);
            try {
                plugin.database.createTownData(new SavedTownData(residentTown.getName(), guild.getId(), townChatChannelId, townChatWebhook, nationChatChannelId, nationChatWebhook, townyLogsChannelId, townyLogsWebhook, townyInfoChannelId, townyInfoWebhook, msg.getId(), rolesString));
            } catch (SQLException e) {
                e.printStackTrace();
                event.getMessage().reply(":warning: | Something went wrong while saving the data to the database. Please contact the server owner.").complete();
                return;
            }

    }
}