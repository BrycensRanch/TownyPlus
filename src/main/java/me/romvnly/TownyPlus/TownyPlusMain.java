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
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.jeff_media.updatechecker.UpdateCheckSource;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static me.romvnly.TownyPlus.util.Constants.UPDATENOTIFICATIONS_PERMISSION;

public final class TownyPlusMain extends JavaPlugin implements Listener {
    public static TownyPlusMain plugin;
    private BukkitAudiences adventure;
    final int metricsId = 14161;
    public FileConfiguration config = getConfig();
    public ChatHook chatHook;
    public UpdateChecker updateChecker;
    public RestAPI restAPI;
    public DiscordSRVListener discordSRVListener = new DiscordSRVListener();


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

    public void onEnable() {
        this.restAPI = new RestAPI(this);
        try {
            this.restAPI.startServer(config.getInt("restapi.port"));
        } catch (Exception e) {
            getLogger().warning("The plugin's Rest API failed to load. :(");
            e.printStackTrace();
        }
        this.adventure = BukkitAudiences.create(this);
        new Metrics(this, metricsId);
        this.updateChecker = new UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, "Romvnly-Gaming/TownyPlus")
                .setChangelogLink("https://github.com/Romvnly-Gaming/TownyPlus/blob/master/CHANGELOG.md")
                .setDonationLink("https://paypal.me/romvnly")
                .setDownloadLink("https://github.com/Romvnly-Gaming/TownyPlus/releases")
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .setNotifyByPermissionOnJoin(UPDATENOTIFICATIONS_PERMISSION)
                .checkEveryXHours(24)
                .checkNow();
        plugin = this;
        try {
            new CommandManager(this);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            this.setEnabled(false);
            return;
        }

        if (!getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        }
        if (config.getBoolean("discordsrv.enabled")) {
            DiscordSRV.api.subscribe(discordSRVListener);
        }
        PluginManager pluginManager = getServer().getPluginManager();
//        if (pluginManager.getPlugin("TownyChat") != null && pluginManager.isPluginEnabled("TownyChat")) {
        chatHook = new TownyChatHook();
        pluginManager.registerEvents(chatHook, this);
//        }
//        if (pluginManager.getPlugin("VentureChat") != null && pluginManager.isPluginEnabled("VentureChat")) {
//            chatHook = new VentureChatHook();
//            // VentureChat overwrites TownyChat or something
//            pluginManager.registerEvents(chatHook, this);
//        }
        new KickedFromTownListener();
        new MayorChangeListener();
        new TownToggleListener();
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
            // check database
            TextChannel textChannel = DiscordSRV.getPlugin().getJda().getTextChannelById("816686639052882022");
            textChannel.sendMessage(String.format("[%s] %s: %s", channel, player.getName(), message)).queue();
        } else {
            try {
                String apiURL = getConfig().getString("externalapi.url");
                if (apiURL == null || apiURL.isBlank())
                    throw new IOException("You didn't provide a VALID URL in your configuration file");
                gson.http(apiURL, "{\"username\": \"" + player.getName() + "\", \"uuid\": \"" + player.getUniqueId() + "\", \"message\": \"" + message + "\", \"channel\": \"" + channelName + "\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}