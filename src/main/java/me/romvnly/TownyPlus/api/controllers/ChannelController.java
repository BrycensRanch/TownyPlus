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
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.api.ChannelManager;
import me.romvnly.TownyPlus.api.RestAPI;
import me.romvnly.TownyPlus.api.StandardResponse;
import me.romvnly.TownyPlus.api.StatusResponse;
import me.romvnly.TownyPlus.api.entities.Channel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import spark.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static me.romvnly.TownyPlus.api.Gson.isJSONValid;
import static spark.Service.ignite;
import static spark.Spark.*;

public class ChannelController {
    public static Service server;

    public static void stopServer() {
        server.stop();
        RestAPI.active = false;
    }

    public ChannelController(TownyPlusMain plugin, Integer portNumber) {
        server = ignite().port(portNumber);
        server.initExceptionHandler((e) -> {
            RestAPI.active = false;
            plugin.getLogger().severe("HTTP server failed to start on port " + portNumber);
            plugin.getLogger().severe(e.toString());
            return;
        });
        server.notFound((req, res) -> {
            return "{\"status\": \"ERROR\", \"message\":\"Page not found. 404!\"}";
        });
        server.internalServerError((req, res) -> {
            return "{\"status\": \"ERROR\", \"message\":\"500 Internal Server Error\"}";
        });

        plugin.getLogger().info("HTTP server started on port " + portNumber);
        RestAPI.active = true;
        final ChannelManager channelManager = new ChannelManager();
        server.before((request, response) -> {
            response.type("application/json");
            JsonObject requestJSONBody = isJSONValid(request.body(), true);
            if (requestJSONBody == null && (request.requestMethod() != "GET" && request.body().length() == 0)) {
                halt(400, new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "Couldn't parse your request body as JSON.")));
            }
            request.attribute("body", requestJSONBody);
        });
        // gets list of all channels
        server.get("/channels", (request, response) -> {
            return new Gson()
                    .toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(channelManager.getChannels())));
        });
        server.post("/channels", (request, response) -> {

            Channel channel = new Gson().fromJson(request.body(), Channel.class);
            return new Gson()
                    .toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJson(channel)));
        });

        server.get("/channels/:id", (request, response) -> new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(channelManager.getChannel(request.params(":id"))))));
        server.post("/handle/message", (request, response) -> {
            JsonObject body = request.attribute("body");

            OfflinePlayer player;
            try {
                player = Bukkit.getOfflinePlayer(UUID.fromString(body.get("player").getAsString()));
            } catch (IllegalArgumentException e) {
                response.status(400);
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "The player variable isn't an UUID."));
            }
            Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
            String channel = body.get("channel").getAsString().trim().toLowerCase();
            if (!channel.equalsIgnoreCase("town") && !channel.equalsIgnoreCase("nation")) {
                response.status(400);
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "The channel variable is only allowed to be town or nation, got " + channel));
            }
            if (player == null) {
                response.status(400);
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "There is no player UUID matching the UUID specified"));
            }
            if (resident == null) {
                response.status(400);
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, "There is no Towny resident for the player specified"));
            }
            String message = body.get("message").getAsString();
            final TextComponent inGameMessage = Component.text()
                    .content(message)
                    .color(TextColor.color(0x443344))
                    .build();
            plugin.getLogger().info("Forward " + channel + " Message: " + message);
            plugin.chatHook.broadcastMessageToChannel(channel, inGameMessage, resident);

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "Success! Handled message!"));
        });
        server.put("/users/:id", (request, response) -> {

            Channel toEdit = new Gson().fromJson(request.body(), Channel.class);
            Channel editedChannel = channelManager.editChannel(toEdit);

            if (editedChannel != null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(editedChannel)));
            } else {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("User not found or error in edit")));
            }
        });

        server.delete("/users/:id", (request, response) -> {

            channelManager.deleteChannel(request.params(":id"));
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "user deleted"));
        });

        server.options("/users/:id", (request, response) -> {

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, (channelManager.channelExist(request.params(":id"))) ? "User exists" : "User does not exists"));
        });
    }
}
