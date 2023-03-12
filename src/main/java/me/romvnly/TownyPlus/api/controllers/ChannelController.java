/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.api.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

import io.javalin.Javalin;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.api.ChannelManager;
import me.romvnly.TownyPlus.api.StandardResponse;
import me.romvnly.TownyPlus.api.StatusResponse;
import me.romvnly.TownyPlus.api.entities.Channel;
import me.romvnly.TownyPlus.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ChannelController extends CrudHandler {

    public ChannelController(TownyPlusMain plugin, Javalin server) {
        final ChannelManager channelManager = new ChannelManager();
        server.get("/channels", ctx -> {
            ctx.json(new Gson()
                        .toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(channelManager.getChannels()))));
        
        });
            server.post("/channels", ctx -> {
    
                Channel channel = new Gson().fromJson(ctx.attribute("body").toString(), Channel.class);
                ctx.json(new Gson()
                        .toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJson(channel))));
            });
    
            server.get("/channels/{id}", ctx -> new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(channelManager.getChannel(ctx.pathParam("id"))))));
            server.post("/channels/{channel}/new/message", ctx -> {
                JsonObject body = ctx.attribute("body");
                Debug.log("Response body for new message");
                Debug.log(body.toString());
    
                OfflinePlayer player;
                try {
                    player = Bukkit.getOfflinePlayer(UUID.fromString(body.get("player").getAsString()));
                } catch (IllegalArgumentException e) {
                    ctx.status(400);
                    ctx.json(new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "The player variable isn't an UUID.")));
                    return;
                }
                Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
                String channel = body.get("channel").getAsString().trim().toLowerCase();
                if (!channel.equalsIgnoreCase("town") && !channel.equalsIgnoreCase("nation")) {
                    ctx.status(400);
                    ctx.json(new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "The channel variable is only allowed to be town or nation, got " + channel)));
                }
                if (resident == null) {
                    ctx.status(400);
                    ctx.json(new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "There is no Towny resident for the player specified")));
                }
                String message = body.get("message").getAsString();
                final TextComponent inGameMessage = Component.text()
                        .content(message)
                        .color(TextColor.color(0x443344))
                        .build();
                Debug.log("Forward " + channel + " Message: " + message);
                plugin.chatHook.broadcastMessageToChannel(channel, inGameMessage, resident.getTownOrNull());
                ctx.status(200);
                ctx.json(new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "Success! Handled message!")));
                return;
            });
            server.put("/channels/users/{id}", ctx -> {
    
                Channel toEdit = new Gson().fromJson(ctx.attribute("body").toString(), Channel.class);
                Channel editedChannel = channelManager.editChannel(toEdit);
    
                if (editedChannel != null) {
                    ctx.status(200);
                    ctx.json( new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(editedChannel))));
                } else {
                    ctx.json( new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("User not found or error in edit"))));
                }
            });
    
            server.delete("/channels/users/{id}", ctx -> {
    
                channelManager.deleteChannel(ctx.pathParam("id"));
                 ctx.json(new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "user deleted")));
            });
    
            server.options("/channels/users/{id}", ctx -> {
    
                ctx.json( new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, (channelManager.channelExist(ctx.pathParam("id"))) ? "User exists" : "User does not exists")));
            });
    }
}
