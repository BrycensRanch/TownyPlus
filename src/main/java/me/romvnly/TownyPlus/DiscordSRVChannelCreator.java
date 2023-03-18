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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.util.DiscordUtil;
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
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiscordSRVChannelCreator {
    private final TownyPlusMain plugin = TownyPlusMain.getInstance();
    // public void initializeGuild(@Nonnull final MessageReceivedEvent event, @Nonnull Guild guild) {) {

    public void createWebhookIfNotExist(@Nonnull final TextChannel channel, @Nonnull String webhookName) {
        if (channel.retrieveWebhooks().complete().stream().filter(webhook -> webhook.getName().equalsIgnoreCase(plugin.getName())).toList().size() == 0) {
            channel.createWebhook(plugin.getName()).complete();
            Debug.log("Created webhook " + webhookName + " for discord channel " + channel.getName());
        }
    }
    public void createCategoryIfNotExist(@Nonnull final Guild guild, @Nonnull String categoryName) {
        if (guild.getCategoriesByName(categoryName, true).size() == 0) {
            guild.createCategory(categoryName).complete();
            Debug.log("Created category " + categoryName + " for discord guild " + guild.getName());
        }
    }
    public void createChannelIfNotExistInCategory(@Nonnull final Category category, @Nonnull String channelName) {
        if (category.getTextChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(channelName)).toList().size() == 0) {
            category.createTextChannel(channelName).complete();
            Debug.log("Created channel " + channelName + " for discord category " + category.getName());
        }
    }
    public void createRoleIfNotExist(@Nonnull final Guild guild, @Nonnull String roleName) {
        if (guild.getRolesByName(roleName, true).size() == 0) {
            guild.createRole().setName(roleName).complete();
            Debug.log("Created role " + roleName + " for discord guild " + guild.getName());
        }
    }
    // }

    public void handleDiscordMessageEvent(@Nonnull final MessageReceivedEvent event) {
        try {
            SavedCode code = plugin.database.findCodeByString(event.getMessage().getContentDisplay());
            if (code == null) {
                Debug.log(Lang.parse("<red>Code is null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
                return;
            }

            Debug.log(Lang.parse("<red>Code is <bold>not</bold> null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            UUID linkedAccountUUID = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getAuthor().getId());
            if (linkedAccountUUID == null || linkedAccountUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                event.getMessage().reply(":warning: | You must link your discord account to your minecraft account before attempting to give the code. Or maybe, you've stolen the code from the town owner? Shame on you.").queue();
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
//            JDA jda = DiscordUtil.getJda();
            Guild guild = event.getGuild();
            String category = "Towny";
            String logsCate = "Logs";

            // Create channels, categories, and webhooks if they don't exist

            createCategoryIfNotExist(guild, category);
            createCategoryIfNotExist(guild, logsCate);

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
            plugin.logger.info(Lang.parse("<user> Redeemed code for <town_formatted> <code>", Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("town_formatted", residentTown.getFormattedName()), Placeholder.unparsed("code", code.getCode())));
            List < Resident > residentList = residentTown.getResidents();
            String formattedResidentList = String.join(", ", residentList.stream().map(Resident::getFormattedName).collect(Collectors.toList()));
            Message msg = townyInfo.sendMessageEmbeds(new EmbedBuilder().addField("Members", formattedResidentList, true).setDescription(String.format("Towny Info channel for %s", residentTown.getFormattedName())).build()).complete();
            String mayorRoleName = residentTown.getName() + " | Mayor";
            String assistantRoleName = residentTown.getName() + " | Assistant";
            String residentRoleName = residentTown.getName() + " | Member";
            createRoleIfNotExist(guild, mayorRoleName);
            createRoleIfNotExist(guild, assistantRoleName);
            createRoleIfNotExist(guild, residentRoleName);
            Role mayorRole = guild.getRolesByName(mayorRoleName, true).get(0);
            Role assistantRole = guild.getRolesByName(assistantRoleName, true).get(0);
            Role residentRole = guild.getRolesByName(residentRoleName, true).get(0);
            ObjectNode roles = TownyPlusMain.JSONMapper.createObjectNode();
            roles.put("mayor", mayorRole.getId());
            roles.put("assistant", assistantRole.getId());
            roles.put("resident", residentRole.getId());
            String rolesString = TownyPlusMain.JSONMapper.writeValueAsString(roles);
            Debug.log("Roles:\n\n" + rolesString);
            try {
                plugin.database.createTownData(new SavedTownData(residentTown.getName(), guild.getId(), townChatChannelId, townChatWebhook, nationChatChannelId, nationChatWebhook, townyLogsChannelId, townyLogsWebhook, townyInfoChannelId, townyInfoWebhook, msg.getId(), rolesString));
            } catch (SQLException e) {
                e.printStackTrace();
                event.getMessage().reply(":warning: | Something went wrong while saving the data to the database. Please contact the server owner.").complete();
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
        if (member == null) {
            Debug.log("No member found for this discord server");
            return;
        }
        final Role topRole = DiscordUtil.getTopRole(member);
        Component discordMessageComponent = MinecraftSerializer.INSTANCE.serialize(event.getMessage().getContentDisplay());
        final TextComponent textComponent = Component.text()
                .content("[" + town.getName() + " Discord" + "] ").color(NamedTextColor.GOLD)
                .append(Component.text().content("[" + event.getAuthor().getName() + " | " + topRole.getName() + "]: ").color(NamedTextColor.RED).build())
                .append(discordMessageComponent)
                .hoverEvent(Component.text().content("This message came from " + town.getName() + "'s Discord Server").color(NamedTextColor.GRAY).build()).build();

        String channelName = event.getChannel().getName().substring("-chat".length());
        if (
                (channelName.contains("town") && savedTownData.getTownChatDiscordID().equalsIgnoreCase(event.getChannel().getId())) ||
                        (channelName.contains("nation") && savedTownData.getNationChatDiscordID().equalsIgnoreCase(event.getChannel().getId()))
        ) {
            TownyPlusMain.plugin.chatHook.broadcastMessageToChannel(channelName, textComponent, town);
        }

    }
}