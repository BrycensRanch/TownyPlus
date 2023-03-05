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
