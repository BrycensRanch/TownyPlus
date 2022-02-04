/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.api;

import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.api.controllers.ChannelController;

public class RestAPI {

    public final TownyPlusMain plugin;

    public RestAPI(TownyPlusMain plugin) {
        this.plugin = plugin;
    }

    public static boolean active = false;
    public static ChannelController channelController;

    public void startServer(Integer port) {
        channelController = new ChannelController(this.plugin, port);
    }

    public void stopServer() {
        channelController.stopServer();
    }
}
