/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Config extends BaseConfig {
    private Config() {
        super("config.yml");
    }

    public static Config config;
    static int version;

    public static void reload() {
        config = new Config();

        version = config.getInt("config-version", 1);
        config.set("config-version", 1);

        config.readConfig(Config.class, null);
    }

    public static String LANGUAGE_FILE = "lang-en.yml";
    public static boolean DEBUG_MODE = false;
    public static boolean AUTO_UPDATE_PLUGIN = false;

    public static boolean CHECK_FOR_UPDATES = true;
    public static boolean METRICS_ENABLED = true;

    public static String githubPAT = "none";


    private static void baseSettings() {
        LANGUAGE_FILE = config.getString("settings.language-file", LANGUAGE_FILE);
        DEBUG_MODE = config.getBoolean("settings.debug-mode", DEBUG_MODE);
        AUTO_UPDATE_PLUGIN = config.getBoolean("settings.auto-update", AUTO_UPDATE_PLUGIN);
        METRICS_ENABLED = config.getBoolean("settings.metrics", METRICS_ENABLED);
        CHECK_FOR_UPDATES = config.getBoolean("settings.update-checker", CHECK_FOR_UPDATES);
        githubPAT = config.getString("settings.github-pat", githubPAT);
    }

    public static boolean HTTPD_ENABLED = true;
    public static String HTTPD_BIND = "127.0.0.1";
    public static int HTTPD_PORT = 8080;

    public static String externalAPIToUse = "none";

    private static void internalWebServerSettings() {
        HTTPD_ENABLED = config.getBoolean("settings.internal-webserver.enabled", HTTPD_ENABLED);
        HTTPD_BIND = config.getString("settings.internal-webserver.bind", HTTPD_BIND);
        HTTPD_PORT = config.getInt("settings.internal-webserver.port", HTTPD_PORT);
        externalAPIToUse = config.getString("settings.internal-webserver.external-api", externalAPIToUse);
    }
    public static boolean DISCORDSRV_ENABLED = true;
    public static String DISCORDSRV_LOG_CHANNEL = "towny-logs";
    public static String DISCORDSRV_WEBHOOK = "https://discord.com/api/webhooks/1234567890/abcdefghijklmnopqrstuvwxyz";
    public static boolean DISCORDSRV_WEBHOOK_ENABLED = false;

    private static void discordSRVIntegrationSettings() {
        DISCORDSRV_ENABLED = config.getBoolean("settings.discordsrv-integration.enabled", DISCORDSRV_ENABLED);
        DISCORDSRV_LOG_CHANNEL = config.getString("settings.discordsrv-integration.log-channel", DISCORDSRV_LOG_CHANNEL);
        DISCORDSRV_WEBHOOK = config.getString("settings.discordsrv-integration.log-webhook", DISCORDSRV_WEBHOOK);
        DISCORDSRV_WEBHOOK_ENABLED = config.getBoolean("settings.discordsrv-integration.log-webhook-enabled", DISCORDSRV_WEBHOOK_ENABLED);
    }

    public static String DB_TYPE = "h2";
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 3306;
    public static String DB_NAME = "townyplus";
    public static String DB_USERNAME = "root";
    public static String DB_PASSWORD = "password";
    public static String DB_TABLE_PREFIX = "townyplus_";
    public static boolean DB_USE_SSL = false;
    public static boolean DB_REQUIRE_SSL = false;
    public static String DB_SSL_MODE = "default";

    public static String DB_URL = "jdbc:h2:./plugins/TownyPlus/data.db";

    private static void databaseSettings() {
        DB_TYPE = config.getString("settings.database.type", DB_TYPE);
        DB_URL = config.getString("settings.database.url", DB_URL);
        DB_HOST = config.getString("settings.database.host", DB_HOST);
        DB_PORT = config.getInt("settings.database.port", DB_PORT);
        DB_NAME = config.getString("settings.database.name", DB_NAME);
        DB_USERNAME = config.getString("settings.database.username", DB_USERNAME);
        DB_PASSWORD = config.getString("settings.database.password", DB_PASSWORD);
        DB_TABLE_PREFIX = config.getString("settings.database.table-prefix", DB_TABLE_PREFIX);
        DB_USE_SSL = config.getBoolean("settings.database.use-ssl", DB_USE_SSL);
        DB_REQUIRE_SSL = config.getBoolean("settings.database.require-ssl", DB_REQUIRE_SSL);
        DB_SSL_MODE = config.getString("settings.database.ssl-mode", DB_SSL_MODE);
    }

    public static String MAIN_COMMAND_LABEL = "townyplus";
    public static List<String> MAIN_COMMAND_ALIASES = new ArrayList<>();

    private static void commandSettings() {
        MAIN_COMMAND_LABEL = config.getString("settings.commands.main-command-label", MAIN_COMMAND_LABEL);
        MAIN_COMMAND_ALIASES.clear();
        config.getList("settings.commands.main-command-aliases", List.of(
                "townplus", "townyp", "tplus", "townp"
        )).forEach(entry -> MAIN_COMMAND_ALIASES.add(entry.toString()));
    }
    public Map<String, Object> outputConfig() {
        return config.outputConfig();
    }

}