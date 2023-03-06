/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.util;

import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.Component;

public class Debug {
    public static void log(String message) {
        if (me.romvnly.TownyPlus.configuration.Config.DEBUG_MODE) {
            TownyPlusMain.plugin.logger.info(message);
        }
    }
    public static void log(Component component) {
        if (me.romvnly.TownyPlus.configuration.Config.DEBUG_MODE) {
            TownyPlusMain.plugin.logger.info(component);
        }
    }
}
