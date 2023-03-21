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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.vdurmont.semver4j.Semver;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.romvnly.TownyPlus.api.RestAPI;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.hooks.chat.ChatHook;
import me.romvnly.TownyPlus.hooks.chat.TownyChatHook;
import me.romvnly.TownyPlus.hooks.chat.VentureChatHook;
import me.romvnly.TownyPlus.listeners.DiscordSRVListener;
import me.romvnly.TownyPlus.listeners.KickedFromTownListener;
import me.romvnly.TownyPlus.listeners.MayorChangeListener;
import me.romvnly.TownyPlus.listeners.TownToggleListener;
import me.romvnly.TownyPlus.util.Debug;
import me.romvnly.TownyPlus.util.GitProperties;
import me.romvnly.TownyPlus.util.WebUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;

import static me.romvnly.TownyPlus.util.Constants.UPDATE_NOTIFICATIONS_PERMISSION;

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
            .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
    .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    public static final ObjectMapper YAMLMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLParser.Feature.EMPTY_STRING_AS_NULL).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)).configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
    public CommandManager commandManager;
    public ChatHook chatHook;
    public UpdateChecker updateChecker;
    public Telemetry telemetry;
    public DiscordSRVChannelCreator discordSRVChannelCreator;
    public RestAPI restAPI;
    public ComponentLogger logger = ComponentLogger.logger(getName());
    public DiscordSRVListener discordSRVListener;
    public Database database;
    public TownyPlusExpansion expansion;
    public String githubRepo;
    public DependencyLoader dependencyLoader = new DependencyLoader();
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
        dependencyLoader.load(this);
        PluginManager pluginManager = getServer().getPluginManager();
        // Order of operations is important here
        // Look mom, I'm using math terms in my code!
        plugin = this;
        this.adventure = BukkitAudiences.create(this);
        logger = ComponentLogger.logger(getName());
        saveDefaultConfig();
        Config.reload();
        Lang.reload();
        if (pluginManager.getPlugin("PlaceholderAPI") != null && pluginManager.isPluginEnabled("PlaceholderAPI")) {

        expansion = new TownyPlusExpansion();
        Boolean didRegisterSuccessfully = expansion.register();
        if (didRegisterSuccessfully) {
            logger.info("Successfully registered with PlaceholderAPI!");
        }
        else {
            logger.warn("Failed to register with PlaceholderAPI!");
        }
        }
        if (Config.DEBUG_MODE) {
            logger.info(Lang.parse(Lang.LOG_DEBUG_MODE_ENABLED));
        }

        try {
         database = new Database();
        }
        catch(Exception e) {
            logger.error("Failed to connect to database. Disabling plugin.");
            e.printStackTrace();
            setEnabled(false);
            return;
        }
        try {
            database.initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        discordSRVChannelCreator = new DiscordSRVChannelCreator();

        // TODO: Use dependency injection to make this less ugly


        String gitRepo = GitProperties.getGitProperty("git.remote.origin.url");
        if (gitRepo == null || gitRepo.isEmpty() || gitRepo.isEmpty() || gitRepo.equals("null")) {
            Lang.sendComponentToConsole(Lang.FAILED_TO_GRAB_GITHUB_REPO, Level.WARNING);
            githubRepo = "BrycensRanch/TownyPlus";
        }
        else {
                githubRepo = gitRepo.replace(".git", "").replace("https://github.com/", "");
        }
        if (!pluginManager.isPluginEnabled("Towny")) {
            logger.error(Lang.parse(Lang.TOWNY_NOT_INSTALLED));
            this.setEnabled(false);
            return;
        }
        if (!unitTest) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    this.restAPI = new RestAPI(this);
                     if (Config.HTTPD_ENABLED) this.restAPI.startServer(Config.HTTPD_BIND, Config.HTTPD_PORT);
                     else logger.info(Lang.parse(Lang.LOG_INTERNAL_WEB_DISABLED));
                } catch (Exception e) {
                    logger.error(Lang.parse(Lang.INTERNAL_WEBSERVER_FAILED_TO_START));
                    e.printStackTrace();
                }
            });
        }
        try {
            telemetry = new Telemetry(this);
            telemetry.load();
        }
        catch (NoClassDefFoundError | Exception e) {
            logger.error(Lang.parse(Lang.LOG_METRICS_FAILED_TO_LOAD));
            e.printStackTrace();
        }
            if (Config.CHECK_FOR_UPDATES) {
                this.updateChecker = new UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, githubRepo)
                        .setChangelogLink(String.format("https://github.com/%s/blob/%s/CHANGELOG.md", githubRepo, GitProperties.getGitProperty("git.branch")))
                        .setDonationLink("https://paypal.me/romvnly")
                        .setDownloadLink(String.format("https://github.com/%s/releases", githubRepo))
                        .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                        .setNotifyByPermissionOnJoin(UPDATE_NOTIFICATIONS_PERMISSION)
                        .checkEveryXHours(12)
                        .checkNow();
            }
            if (Config.AUTO_UPDATE_PLUGIN && Config.CHECK_FOR_UPDATES) {
                if (this.updateChecker.isUsingLatestVersion() || !Config.AUTO_UPDATE_PLUGIN || getDescription().getVersion().contains("SNAPSHOT")) {
                    Debug.log("Not updating plugin, either already on latest version or auto update is disabled");
                    return;
                }
                possiblyAutoUpdate(this, adventure().permission(UPDATE_NOTIFICATIONS_PERMISSION), this.updateChecker.getLatestVersion());
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (updateChecker.isUsingLatestVersion() || !Config.AUTO_UPDATE_PLUGIN || getDescription().getVersion().contains("SNAPSHOT")) {
                            Debug.log("Not updating plugin, either already on latest version or auto update is disabled");
                            return;
                        }
                        possiblyAutoUpdate(TownyPlusMain.this, adventure().permission(UPDATE_NOTIFICATIONS_PERMISSION), updateChecker.getLatestVersion());
                    }
                };
                runnable.runTaskTimer(this, 0, 20 * 60 * 60 * 12);
            }
        try {
            this.commandManager = new CommandManager(this);
        } catch (Exception e) {
            logger.error(Lang.parse(Lang.COMMAND_MANAGER_FAILED_TO_INITIALIZE));
            e.printStackTrace();
            this.setEnabled(false);
            return;
        }

        if (!getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        }
        pluginManager = getServer().getPluginManager();
        if (!unitTest) {
            if (Config.DISCORDSRV_ENABLED && pluginManager.getPlugin("DiscordSRV") != null && pluginManager.isPluginEnabled("DiscordSRV")) {
                discordSRVListener = new DiscordSRVListener(this, this.commandManager);
                DiscordSRV.api.subscribe(discordSRVListener);
            } else if (Config.DISCORDSRV_ENABLED) {
                logger.warn(Lang.parse(Lang.DISCORDSRV_NOT_INSTALLED));
            }
            if (pluginManager.getPlugin("TownyChat") != null && pluginManager.isPluginEnabled("TownyChat")) {
                chatHook = new TownyChatHook();
                pluginManager.registerEvents(chatHook, this);
            }
       else {
           logger.warn(Lang.parse(Lang.TOWNYCHAT_NOT_INSTALLED));
       }
            if (pluginManager.getPlugin("VentureChat") != null && pluginManager.isPluginEnabled("VentureChat")) {
                chatHook = new VentureChatHook();
                // VentureChat overwrites TownyChat or something
                pluginManager.registerEvents(chatHook, this);
            }
         else {
           logger.warn(Lang.parse(Lang.VENTURECHAT_NOT_INSTALLED));
       }
            new KickedFromTownListener();
            new MayorChangeListener();
            new TownToggleListener();
        }
        // I will not be adding this to the localization file. It's just a message to let the user know that the plugin is enabled.
        logger.info("----------------------------------------");
        logger.info(Lang.parse("<gold>" + getDescription().getName() + "<green> Enabled!"));
        logger.info(Lang.parse("<rainbow>Version: " + getDescription().getVersion()));
        logger.info(Lang.parse("<blue>Author: " + getDescription().getAuthors()));
        logger.info(Lang.parse("GitHub: " + getDescription().getWebsite()));
        logger.info(Lang.parse("<underlined>Any issues or suggestions? Report them here: " + getDescription().getWebsite() + "/issues"));
        if (this.restAPI != null) logger.warn(Lang.parse("<green>Any messages related to Jetty are normal. They are <bold>not</bold> errors. <gradient:green:blue>They are just letting you know that the internal webserver is running. If you want to disable it, you'll have to disable the internal-webserver in the config. I can't do anything about the logs when it's running. Sorry.</gradient>"));
        logger.info("----------------------------------------");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        logger.info("Player joined.");
    }

    public void onDisable() {
        if (this.isEnabled()) {
            // The idea: If the plugin calls onDisable() itself, it will disable the plugin without having to call Bukkit.getServer().getPluginManager().disablePlugin(this);
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        if (this.updateChecker != null) {
            this.updateChecker.stop();
        }
        if (this.restAPI != null) { this.restAPI.stopServer(); }
        if (discordSRVListener != null) {
            DiscordSRV.api.unsubscribe(discordSRVListener);
        }
        if (this.database != null) this.database.close();
        if (expansion != null)
            expansion.unregister();
        // I haVE NO IDEA WHY THIS IS HERE BUT I'M NOT TOUCHING IT
        Bukkit.getScheduler().cancelTasks(this);
        logger.info(Lang.parse("<red>Plugin has been disabled."));
        this.commandManager = null;
        // Should be done last to prevent any errors whilst disabling the plugin.
        plugin = null;
    }
    public void possiblyAutoUpdate(JavaPlugin plugin, Audience commandSenders, String latestVersion) {
        logger.info(Lang.parse(Lang.ATTEMPTING_TO_AUTO_UPDATE));
         commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, Lang.ATTEMPTING_TO_AUTO_UPDATE));
        String releaseJSONString = WebUtils.getBody("https://api.github.com/repos/" + githubRepo + "/releases");
        try {
            JsonNode releaseJSONArray = JSONMapper.readTree(releaseJSONString);
            JsonNode latestRelease = null;
            Integer index = 0;
            Integer numberOfReleasesToSearch = Integer.MAX_VALUE;
            for (int i = 0; i < numberOfReleasesToSearch; i++) {
                JsonNode node = releaseJSONArray.get(index);
                String targetCommitish = node.get("target_commitish").asText();
                String targetBranch = GitProperties.getGitProperty("git.branch");
                if (targetBranch == null || targetBranch.isBlank() || targetBranch.isBlank()|| targetBranch.equals("HEAD")) {
                    if (getDescription().getVersion().contains("alpha"))
                        targetBranch = "alpha";
                    else if (getDescription().getVersion().contains("beta"))
                        targetBranch = "beta";
                    else if (getDescription().getVersion().contains("rc"))
                        targetBranch = "rc";
                    else
                        targetBranch = "master";
                }
                if (targetBranch.equalsIgnoreCase(targetCommitish)) {
                    latestRelease = node;
                    break;
                }
                else {
                    Debug.log("Skipping release " + node.get("tag_name").asText() + " because it's not on the " + targetBranch + " branch. (target_commitish: " + targetCommitish + ")");
                }

            }
            if (latestRelease == null) {
                Component errorMessage = Lang.parse(Lang.FAILED_TO_AUTO_UPDATE_REASON, Placeholder.unparsed("plugin", getName()), Placeholder.unparsed("reason", "There is no matching release matching release channel."));
                logger.warn(errorMessage);
                commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, errorMessage));
                return;
            }
            String latestVersionName = latestRelease.get("tag_name").asText();
            JsonNode releasePluginGitHubAsset = null;
            for (int i = 0; i < numberOfReleasesToSearch; i++) {
                JsonNode node = latestRelease.get("assets").get(i).deepCopy();
                String jarName = node.get("name").asText();
                if (!jarName.contains("sources") && !jarName.contains("javadoc") && jarName.contains("jar")) {
                    releasePluginGitHubAsset = node;
                    break;
                }
            }
            if (releasePluginGitHubAsset == null) {
                Component errorMessage = Lang.parse(Lang.FAILED_TO_AUTO_UPDATE_REASON, Placeholder.unparsed("plugin", getName()), Placeholder.unparsed("reason", "The latest matching release does not contain a matching jar file."));
                logger.warn(errorMessage);
                commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, errorMessage));
                return;
            }

            File updateFile = new File("plugins/update/" + releasePluginGitHubAsset.get("name").asText());
            String updateFileVersion = updateFile.getName().split("-")[1].replace(".jar", "");
            Semver currentVersionSemver = new Semver(getDescription().getVersion(), Semver.SemverType.NPM);
            Semver updateFileVersionSemver = new Semver(updateFileVersion, Semver.SemverType.NPM);
            if (updateFileVersionSemver.isLowerThan(getDescription().getVersion())) {
                Component errorMessage = Lang.parse(Lang.SEMVER_NOT_HIGHER, Placeholder.unparsed("current", getDescription().getVersion()), Placeholder.unparsed("version", updateFileVersion));
                logger.warn(errorMessage);
                commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, errorMessage));
                return;
            }
            if (!currentVersionSemver.getMajor().toString().equals(updateFileVersionSemver.getMajor().toString())) {
                Component errorMessage = Lang.parse(Lang.SEMVER_DO_NOT_AUTO_UPDATE_ON_MAJOR, Placeholder.unparsed("current", getDescription().getVersion()), Placeholder.unparsed("version", updateFileVersion));
                logger.warn(errorMessage);
                commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, errorMessage));
            }

            if (updateFile.isFile()) {
                Component errorMessage = Lang.parse(Lang.FAILED_TO_AUTO_UPDATE_REASON, Placeholder.unparsed("plugin", getName()), Placeholder.unparsed("reason", "The latest matching release has already been downloaded."));
                logger.warn(errorMessage);
                commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, errorMessage));
                return;
            }
                String downloadLink = releasePluginGitHubAsset.get("browser_download_url").asText();
                URL downloadURL;
                try {
                    downloadURL = new URL(downloadLink);
                    Component message = Component.text().content(String.format("Downloading %s %s ...", plugin.getName(), latestVersionName)).build();
                    logger.info(message);
                    commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, message));
                } catch (MalformedURLException e) {
                    Component errorMessage = Lang.parse(Lang.FAILED_TO_AUTO_UPDATE_REASON, Placeholder.unparsed("plugin", getName()), Placeholder.unparsed("reason", "The download link is malformed"));
                    logger.warn(errorMessage);
                    commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, errorMessage));
                    return;
                }
                // todo: make sure we use the update folder defined in bukkit.yml (it can be changed)

                FileUtils.copyURLToFile(downloadURL, updateFile);
                Component message = Lang.parse(Lang.SUCCESSFULLY_AUTO_UPDATED, Placeholder.unparsed("plugin", plugin.getName()), Placeholder.unparsed("version", latestVersionName));
                logger.info(message);
                commandSenders.forEachAudience(commandSender -> Lang.send(commandSender, message));
            }
         catch (Exception e) {
             e.printStackTrace();
         }
    }
    @SuppressWarnings("unused")
    public void processChatMessage(Player player, String providedMessage, String channel, ChatHook hook) {
        // Don't process messages if DiscordSRV isn't ready or in the classpath.
        if (this.discordSRVListener == null) return;

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
        if (Config.DISCORDSRV_ENABLED) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            // check database
            TextChannel textChannel = DiscordSRV.getPlugin().getJda().getTextChannelById("1079260674078810182");
            if (textChannel != null) {
                textChannel.sendMessage(String.format("[%s] %s: %s", channel, player.getName(), message)).queue();
            }
        });
        } else {
            try {
                String externalAPIURL = Config.externalAPIToUse;
                String apiURL;
                if (externalAPIURL.toLowerCase().equalsIgnoreCase("none")) {
                    apiURL = String.format("http://%s:%s", Config.HTTPD_BIND, Config.HTTPD_PORT);
                }
                else {
                    apiURL = Config.externalAPIToUse;
                }
                if (externalAPIURL.isBlank()) throw new IOException("You didn't provide a VALID URL in your configuration file");
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

                try {
                    JsonNode body = JSONMapper.createObjectNode()
                            .put("username", player.getName())
                            .put("uuid", player.getUniqueId().toString())
                            .put("message", message)
                            .put("channel", channelName);
                    gson.http(apiURL + String.format("/channels/%s/new/message", channel), JSONMapper.writeValueAsString(body));
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