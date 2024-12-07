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

import me.romvnly.TownyPlus.configuration.Config;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.logging.LogLevel;

public class DependencyLoader {
    private String basePackage = "me{}romvnly{}TownyPlus";
    private String relocationBasePackage = basePackage + "{}libs";
    public void load(TownyPlusMain plugin) {

        // Load dependencies here
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(plugin);
        if (Config.DEBUG_MODE) libraryManager.setLogLevel(LogLevel.DEBUG);
        libraryManager.addMavenCentral();

        // You've done this to yourself. You're welcome.
        // Shouldn't of been such a large library to shade.
        // smh
        String ifGroupId = "com{}github{}stefvanschie{}inventoryframework";
        var jacksonGroupId = "com{}fasterxml{}jackson";
        var jacksonVersion = "2.18.2";
        String ifRelocation = relocationBasePackage + "{}inventoryframework";
        String jacksonReelocation = relocationBasePackage + "{}fasterxml{}jackson";

        Library IF = Library.builder()
                .groupId(ifGroupId) // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("IF")
                .version("0.10.18")
                .relocate(ifGroupId, ifRelocation) // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .build();
        Library jacksondatabind = Library.builder()
                .groupId(jacksonGroupId + "{}core") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("jackson-databind")
                .version(jacksonVersion)
                // .relocate(jacksonGroupId + "{}core", jacksonReelocation)

                .build();
        Library jacksondataformatYAML = Library.builder()
                .groupId(jacksonGroupId + "{}dataformat") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("jackson-dataformat-yaml")
                .version(jacksonVersion)
                // .relocate(jacksonGroupId + "{}dataformat", jacksonReelocation)

                .build();
        Library jacksondataformatPropeties = Library.builder()
                .groupId(jacksonGroupId + "{}dataformat") // "{}" is replaced with ".", useful to avoid unwanted changes made by maven-shade-plugin
                .artifactId("jackson-dataformat-properties")
                // .relocate(jacksonGroupId + "{}dataformat", jacksonReelocation)
                .version(jacksonVersion)
                .build();
        // libraryManager.loadLibrary(jacksondatabind);
        // libraryManager.loadLibrary(jacksondataformatYAML);
        // libraryManager.loadLibrary(jacksondataformatPropeties);
        libraryManager.loadLibrary(IF);
    }
}
