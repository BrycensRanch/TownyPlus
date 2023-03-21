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

import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.util.GitProperties;
import org.apache.commons.lang.BooleanUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class Telemetry {
    private static Metrics metrics;
    private static TownyPlusMain plugin;

    public Telemetry(TownyPlusMain plugin) {
        this.plugin = plugin;
    }
    final int metricsId = 14161;
    public void load() {
        // Load telemetry here
            if (Config.METRICS_ENABLED) {
                metrics = new Metrics(plugin, metricsId);
                metrics.addCustomChart(new SimplePie("language_used", () ->
                        Config.LANGUAGE_FILE.replace("lang-", "").replace(".yml", "")
                ));
                metrics.addCustomChart(new SimplePie("internal_web_server", () ->
                        BooleanUtils.toStringTrueFalse(Config.HTTPD_ENABLED)
                ));
                metrics.addCustomChart(new SimplePie("branch", () ->
                        GitProperties.getGitProperty("git.branch")
                ));
                metrics.addCustomChart(new SimplePie("database_type", () ->
                        Config.DB_TYPE
                ));
                metrics.addCustomChart(new SimplePie("checking_for_updates", () ->
                        BooleanUtils.toStringTrueFalse(Config.CHECK_FOR_UPDATES)
                ));
                metrics.addCustomChart(new SimplePie("auto_updating", () ->
                        BooleanUtils.toStringTrueFalse(Config.AUTO_UPDATE_PLUGIN)
                ));
                metrics.addCustomChart(new SimplePie("discordsrv_integration", () ->
                        BooleanUtils.toStringTrueFalse(Config.DISCORDSRV_ENABLED)
                ));
            }
    }
}
