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

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

public class DependencyLoader {
    private String basePackage = "me{}romvnly{}TownyPlus";
    private String relocationBasePackage = basePackage + "{}libs";
    public void load(TownyPlusMain plugin) {
        // Load dependencies here
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(plugin);
        libraryManager.addMavenCentral();

        // You've done this to yourself. You're welcome.
        // Shouldn't of been such a large library to shade.
        // smh
        String ifGroupId = "com{}github{}stefvanschie{}inventoryframework";
        String ifRelocation = relocationBasePackage + "{}inventoryframework";
        Library IF = Library.builder()
                .groupId(ifGroupId) // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("IF")
                .version("0.10.8")
                .relocate(ifGroupId, ifRelocation) // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .build();
        libraryManager.loadLibrary(IF);
    }
}
