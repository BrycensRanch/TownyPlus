/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SavedTownData {
    // Town name
    @JsonProperty("name")
    private String name;

    // TODO: Hash/encrypt webhooks as they are sensitive data
    @JsonProperty("discord_server")
     private String townDiscordServerID;
        @JsonProperty("town_chat_id")
     private String townChatDiscordID;
        @JsonProperty("town_chat_webhook_url")
     private String townChatWebhookURL;
        @JsonProperty("nation_chat_id")
     private String nationChatDiscordID;
        @JsonProperty("towny_log_channel_id")
     private String townyLogChannelDiscordID;
        @JsonProperty("towny_log_webhook_url")
     private String townyLogChannelWebhookURL;
        @JsonProperty("towny_info_channel_id")
     private String townInfoChannelDiscordID;
        @JsonProperty("towny_info_channel_webhook")
     private String townInfoChannelWebhookURL;
        @JsonProperty("town_info_channel_message_id")
     private String townInfoChannelMessageID;
        @JsonProperty("town_discord_roles")
        private String townDiscordRoles;

}