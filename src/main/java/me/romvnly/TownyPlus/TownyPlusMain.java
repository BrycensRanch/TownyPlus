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

import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import com.palmergames.bukkit.metrics.bukkit.Metrics;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.romvnly.TownyPlus.api.RestAPI;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.hooks.chat.ChatHook;
import me.romvnly.TownyPlus.hooks.chat.TownyChatHook;
import me.romvnly.TownyPlus.hooks.chat.VentureChatHook;
import me.romvnly.TownyPlus.listeners.DiscordSRVListener;
import me.romvnly.TownyPlus.listeners.KickedFromTownListener;
import me.romvnly.TownyPlus.listeners.MayorChangeListener;
import me.romvnly.TownyPlus.listeners.TownToggleListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.updatechecker.UpdateCheckSource;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static me.romvnly.TownyPlus.util.Constants.UPDATENOTIFICATIONS_PERMISSION;

public final class TownyPlusMain extends JavaPlugin implements Listener {
    private final boolean unitTest;

    public TownyPlusMain() {
        super();
        unitTest = false;
    }

    protected TownyPlusMain(
            JavaPluginLoader loader,
            PluginDescriptionFile description,
            File dataFolder,
            File file) {
        super(loader, description, dataFolder, file);
        unitTest = true;
    }
    public static TownyPlusMain plugin;
    private BukkitAudiences adventure;
    public static final ObjectMapper JSONMapper = new ObjectMapper()
    .enable(JsonParser.Feature.IGNORE_UNDEFINED)
    .enable(JsonParser.Feature.ALLOW_COMMENTS)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
    .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    final int metricsId = 14161;
    public FileConfiguration config = getConfig();
    public CommandManager commandManager;
    public ChatHook chatHook;
    public UpdateChecker updateChecker;
    public RestAPI restAPI;
    public DiscordSRVListener discordSRVListener = new DiscordSRVListener(this, this.commandManager);    
    public @NonNull
    BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public static TownyPlusMain getInstance() {
        return plugin;
    }
    public boolean isUnitTest() {
        return unitTest;
    }

    public void onEnable() {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                this.restAPI = new RestAPI(this);
                this.restAPI.startServer(config.getString("restapi.host"), config.getInt("restapi.port"));
                }
                catch (Exception e) {
                    getLogger().warning("The plugin's Rest API failed to load. :(");
                    e.printStackTrace();
                }
            });
        this.adventure = BukkitAudiences.create(this);
        if (!unitTest) {
            new Metrics(this, metricsId);
            this.updateChecker = new UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, "Romvnly-Gaming/TownyPlus")
            .setChangelogLink("https://github.com/BrycensRanch/TownyPlus/blob/master/CHANGELOG.md")
            .setDonationLink("https://paypal.me/romvnly")
            .setDownloadLink("https://github.com/BrycensRanch/TownyPlus/releases")
            .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
            .setNotifyByPermissionOnJoin(UPDATENOTIFICATIONS_PERMISSION)
            .checkEveryXHours(24)
            .checkNow();
        }
        plugin = this;
        try {
         this.commandManager = new CommandManager(this);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            this.setEnabled(false);
            return;
        }

        if (!getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        }
        if (!unitTest) {
        if (config.getBoolean("discordsrv.enabled")) {
            DiscordSRV.api.subscribe(discordSRVListener);
        }
        PluginManager pluginManager = getServer().getPluginManager();
       if (pluginManager.getPlugin("TownyChat") != null && pluginManager.isPluginEnabled("TownyChat")) {
        chatHook = new TownyChatHook();
                pluginManager.registerEvents(chatHook, this);
       }
       if (pluginManager.getPlugin("VentureChat") != null && pluginManager.isPluginEnabled("VentureChat")) {
           chatHook = new VentureChatHook();
           // VentureChat overwrites TownyChat or something
           pluginManager.registerEvents(chatHook, this);
       }
        new KickedFromTownListener();
        new MayorChangeListener();
        new TownToggleListener();
    }
        getLogger().info("TownyPlus has been Enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getLogger().info("Player joined.");
    }

    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        plugin = null;
        this.updateChecker.stop();
        this.restAPI.stopServer();
        if (config.getBoolean("discordsrv.enabled")) {
            DiscordSRV.api.unsubscribe(discordSRVListener);
        }
        getLogger().info("TownyPlus has been Disabled!");
    }

    public void processChatMessage(Player player, String providedMessage, String channel, ChatHook hook) {
        String message = providedMessage.trim();
        String channelName = channel.toLowerCase().trim();
        if (player == null) return;
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        // This is configured to do POST requests by default...
        me.romvnly.TownyPlus.api.Gson gson = new me.romvnly.TownyPlus.api.Gson();
        if (channelName.equalsIgnoreCase("town")) {
            assert resident != null;
            Town town = resident.getTownOrNull();
            if (town == null) return;
        }
        if (channelName.equalsIgnoreCase("nation")) {
            assert resident != null;
            Nation nation = resident.getNationOrNull();
            if (nation == null) return;
        }
        // We only want town/nation messages
//        if (!List.of("town", "nation").contains(channelName)) return;
        if (config.getBoolean("discordsrv.enabled")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            // check database
            TextChannel textChannel = DiscordSRV.getPlugin().getJda().getTextChannelById("1079260674078810182");
            if (textChannel != null) {
                textChannel.sendMessage(String.format("[%s] %s: %s", channel, player.getName(), message)).queue();
            }
        });
        } else {
            try {
                String externalAPIURL = getConfig().getString("restapi.externalURL");
                String apiURL;
                if (externalAPIURL.toLowerCase().contains("none")) {
                    apiURL = String.format("http://%s:%s", "127.0.0.1", getConfig().getString("restapi.port"));
                }
                else {
                    apiURL = getConfig().getString("restapi.externalURL");
                }
                if (externalAPIURL == null || externalAPIURL.isBlank())
                    throw new IOException("You didn't provide a VALID URL in your configuration file");
                    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

                try {
                    gson.http(apiURL + String.format("/channels/%s/new/message", channel), "{\"username\": \"" + player.getName() + "\", \"uuid\": \"" + player.getUniqueId() + "\", \"message\": \"" + message + "\", \"channel\": \"" + channelName + "\"}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}