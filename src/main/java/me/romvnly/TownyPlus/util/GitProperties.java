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

package me.romvnly.TownyPlus.util;

import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GitProperties {
    public static String getGitProperty(String property) {
        try (InputStream stream = GitProperties.class.getClassLoader().getResourceAsStream("git.properties")) {
            Properties gitProp = new Properties();
            gitProp.load(stream);
            return gitProp.getProperty(property);

        } catch (IOException | AssertionError | NumberFormatException | NullPointerException e) {
            TownyPlusMain.getInstance().getLogger().warning("Could not get git properties!");
            e.printStackTrace();
            return null;
        }
    }
}
