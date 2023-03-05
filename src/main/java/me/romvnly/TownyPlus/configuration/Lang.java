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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import me.romvnly.TownyPlus.TownyPlusMain;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.checkerframework.checker.nullness.qual.NonNull;

import static me.romvnly.TownyPlus.TownyPlusMain.plugin;

public final class Lang {

    // MiniMessage formatted strings, to be sent using Logger.info(String, Template...) or Lang.send(CommandSender, String, Template...)
    @LangKey("description")
    public static String BUKKIT_COMMAND_DESCRIPTION = "It's time to upgrade your Towny server. '/townyplus help' for more information";

    @LangKey("attempting-to-auto-update")
    public static String ATTEMPTING_TO_AUTO_UPDATE = "<green>Attempting to auto-update <plugin>...";
    @LangKey("prefix")
    public static String PREFIX = "<gold>[<plugin>] <reset>";

    @LangKey("no-permission")
    public static String NO_PERMISSION = "<red>You do not have permission to use this command.";

    @LangKey("command.bypass-description")
    public static String COMMAND_BYPASS_DESCRIPTION = "Bypass towny restrictions temporarily";

    @LangKey("failed-to-auto-update")
    public static String FAILED_TO_AUTO_UPDATE = "<red>Failed to auto-update <plugin>. Please update manually.";
    @LangKey("failed-to-auto-update-reason")
    public static String FAILED_TO_AUTO_UPDATE_REASON = "<red>Failed to auto-update <plugin>. Reason: <reason>";

    @LangKey("successfully-auto-updated")
    public static String SUCCESSFULLY_AUTO_UPDATED = "<green>Successfully auto-updated <plugin> to version <version>. It will take effect on the next server restart.";

    @LangKey("failed-to-grab-github-repo")
    public static String FAILED_TO_GRAB_GITHUB_REPO = "<red>Failed to get GitHub repo URL from git.properties. Using default.";
    @LangKey("towny-not-installed")
    public static String TOWNY_NOT_INSTALLED = "<red>Towny is not installed. Please install Towny to use <plugin>.";

    @LangKey("semver-not-higher")
    public static String SEMVER_NOT_HIGHER = "<red>Version <version> is not higher than the current version <current>.";

    @LangKey("semver-do-not-auto-update-on-major")
    public static String SEMVER_DO_NOT_AUTO_UPDATE_ON_MAJOR = "<red>Version <version> is a major version update. Auto-updating on major version updates is DEFINITELY NOT SUPPORTED. Please update manually.";


    @LangKey("internal-webserver-failed-to-start")
    public static String INTERNAL_WEBSERVER_FAILED_TO_START = "<red>Internal webserver failed to start. Please report this to the plugin author.";
    @LangKey("command-manager-failed-to-initialize")
    public static String COMMAND_MANAGER_FAILED_TO_INITIALIZE = "<red>Command manager failed to initialize. Please report this to the plugin author.";
    @LangKey("discordsrv-not-installed")
    public static String DISCORDSRV_NOT_INSTALLED = "<red>DiscordSRV is not installed or enabled. DiscordSRV integration will not work. After installing DiscordSRV, please restart your server.";
    @LangKey("townychat-not-installed")
    public static String TOWNYCHAT_NOT_INSTALLED = "<red>TownyChat is not installed or enabled. TownyChat hooks will not work.";
    @LangKey("venturechat-not-installed")
    public static String VENTURECHAT_NOT_INSTALLED = "<red>VentureChat is not installed or enabled. VentureChat hooks will not work. After installing VentureChat, please restart your server.";
    @LangKey("placeholderapi-not-installed")
    public static String PLACEHOLDERAPI_NOT_INSTALLED = "<red>PlaceholderAPI is not installed or enabled. PlaceholderAPI hooks will not work. After installing PlaceholderAPI, please restart your server.";
    @LangKey("not-in-town")
    public static String NOT_IN_TOWN = "<red>You are not in a town. You may not use this command.";
    @LangKey("not-town-mayor")
    public static String NOT_TOWN_MAYOR = "<red>You are not the mayor of your town. You may not use this command.";

    @LangKey("not-linked-to-discord")
    public static String NOT_LINKED_TO_DISCORD = "<red>You are not linked to Discord. You may not use this command. Please link your account using /discord link";
    @LangKey("link-discord-to-town")
    public static String LINK_DISCORD_TO_TOWN = "<green>Invite the Discord bot to your town's Discord server (<town>) and say <code> in the server anywhere the bot can see it. You have <time> to do this.";
    @LangKey("town-already-linked")
    public static String TOWN_ALREADY_LINKED = "<red>Your town is already linked to a Discord server.";
    @LangKey("render-not-in-progress")
    public static String RENDER_NOT_IN_PROGRESS = "<red>No renders running for <world>";
    @LangKey("cancelled-render")
    public static String CANCELLED_RENDER = "<green>Render cancelled for <world>";
    @LangKey("successfully-reset-map")
    public static String SUCCESSFULLY_RESET_MAP = "<green>Successfully reset map for <world>";
    @LangKey("paused-renders")
    public static String PAUSED_RENDER = "<green>Paused renders for <world>";
    @LangKey("unpaused-renders")
    public static String UNPAUSED_RENDER = "<green>Unpaused renders for <world>";

    @LangKey("command.message.console-must-specify-player")
    public static String CONSOLE_MUST_SPECIFY_PLAYER = "<red>You must specify a target player when running this command from console";
    @LangKey("command.message.player-not-found-for-input")
    public static String PLAYER_NOT_FOUND_FOR_INPUT = "<red>No player found for input '<input>'";
    @LangKey("command.message.console-must-specify-world")
    public static String CONSOLE_MUST_SPECIFY_WORLD = "<red>You must specify the world when running this command from console";
    @LangKey("command.message.no-such-world")
    public static String NO_SUCH_WORLD = "<red>No such world '<world>'";
    @LangKey("command.message.map-not-enabled-for-world")
    public static String MAP_NOT_ENABLED_FOR_WORLD = "<red>Map is not enabled for world '<world>'";
    @LangKey("command.message.confirmation-required")
    public static String CONFIRMATION_REQUIRED_MESSAGE = "<red>Confirmation required. Confirm using /<command> confirm.";
    @LangKey("command.message.no-pending-commands")
    public static String NO_PENDING_COMMANDS_MESSAGE = "<red>You don't have any pending commands.";

    @LangKey("click-for-help")
    public static String CLICK_FOR_HELP = "Click for help";
    @LangKey("click-to-confirm")
    public static String CLICK_TO_CONFIRM = "Click to confirm";

    @LangKey("command.prefix")
    public static String COMMAND_PREFIX = "<white>[<gradient:#C028FF:#5B00FF>Pl3xMap</gradient>]</white> ";
    @LangKey("command.description.help")
    public static String HELP_COMMAND_DESCRIPTION = "Get help for Pl3xmap commands";
    @LangKey("command.description.resetmap")
    public static String RESETMAP_COMMAND_DESCRIPTION = "Resets the map of a specified world";
    @LangKey("command.description.confirm")
    public static String CONFIRM_COMMAND_DESCRIPTION = "Confirm a pending command";
    @LangKey("command.description.reload")
    public static String RELOAD_COMMAND_DESCRIPTION = "Reloads the plugin";
    @LangKey("command.description.full-render")
    public static String FULLRENDER_COMMAND_DESCRIPTION = "Starts a full render for the specified world";
    @LangKey("command.description.cancel-render")
    public static String CANCEL_RENDER_COMMAND_DESCRIPTION = "Cancels a render for the specified world";
    @LangKey("command.description.pause-render")
    public static String PAUSE_RENDER_COMMAND_DESCRIPTION = "Pauses all renders for the specified world";
    @LangKey("command.argument.optional-world")
    public static String OPTIONAL_WORLD_ARGUMENT_DESCRIPTION = "Defaults to the players current world if not provided";
    @LangKey("command.description.radius-render")
    public static String RADIUSRENDER_COMMAND_DESCRIPTION = "Starts a radius render";
    @LangKey("command.argument.optional-center")
    public static String OPTIONAL_CENTER_ARGUMENT_DESCRIPTION = "Defaults to (<white>0<gray>,</gray> 0</white>) if unspecified";
    @LangKey("command.argument.optional-player")
    public static String OPTIONAL_PLAYER_ARGUMENT_DESCRIPTION = "Defaults to the executing player if unspecified (console must specify a player)";
    @LangKey("command.argument.help-query")
    public static String HELP_QUERY_ARGUMENT_DESCRIPTION = "Help Query";

    @LangKey("command.description.hide")
    public static String HIDE_COMMAND_DESCRIPTION = "Hide a player from the map view";
    @LangKey("command.hide.already-hidden")
    public static String PLAYER_ALREADY_HIDDEN = "<red><player> is already hidden from map";
    @LangKey("command.hide.hidden")
    public static String PLAYER_HIDDEN = "<green><player> is now hidden from map";

    @LangKey("command.description.show")
    public static String SHOW_COMMAND_DESCRIPTION = "Show a player on the map view";
    @LangKey("command.show.not-hidden")
    public static String PLAYER_NOT_HIDDEN = "<red><player> is not hidden from map";
    @LangKey("command.show.shown")
    public static String PLAYER_SHOWN = "<green><player> is no longer hidden from map";

    @LangKey("ui.page-title")
    public static String UI_TITLE = "Pl3xMap - {world}";
    @LangKey("ui.coordinates")
    public static String UI_COORDINATES_HTML = "Coordinates<br/>{x}, {z}";
    @LangKey("ui.sidebar.player-list")
    public static String UI_SIDEBAR_PLAYER_LIST_LABEL = "Players ({cur}/{max})";
    @LangKey("ui.sidebar.world-list")
    public static String UI_SIDEBAR_WORLD_LIST_LABEL = "Worlds";
    @LangKey("ui.layers.player-tracker")
    public static String UI_PLAYER_TRACKER_LABEL = "Players";
    @LangKey("ui.layers.world-spawn")
    public static String UI_SPAWN_MARKER_ICON_LABEL = "Spawn";
    @LangKey("ui.layers.world-border")
    public static String UI_WORLDBORDER_MARKER_LABEL = "World Border";

    @LangKey("plugin-reloaded")
    public static String PLUGIN_RELOADED = "<green><name> v<version> reloaded";

    @LangKey("log.prefix")
    public static String LOGGER_PREFIX = "<dark_aqua>[<light_purple>Pl3xMap</light_purple>]</dark_aqua>";

    @LangKey("log.started-full-render")
    public static String LOG_STARTED_FULLRENDER = "<dark_aqua>Started full map render for <yellow><world>";
    @LangKey("log.started-radius-render")
    public static String LOG_STARTED_RADIUSRENDER = "<dark_aqua>Started radius map render for <yellow><world>";
    @LangKey("log.scanning-region-files")
    public static String LOG_SCANNING_REGION_FILES = "<yellow>Scanning region files... (this may take a moment)";
    @LangKey("log.found-total-region-files")
    public static String LOG_FOUND_TOTAL_REGION_FILES = "<green>Found <gray><total> <green>region files";
    @LangKey("log.finished-rendering")
    public static String LOG_FINISHED_RENDERING = "<dark_aqua>Finished rendering map for <yellow><world>";
    @LangKey("log.cancelled-rendering")
    public static String LOG_CANCELLED_RENDERING = "<dark_aqua>Rendering map for <yellow><world> <dark_aqua>has been interrupted";
    @LangKey("log.resumed-rendering")
    public static String LOG_RESUMED_RENDERING = "<dark_aqua>Rendering map for <yellow><world> <dark_aqua>has been resumed";
    @LangKey("log.scanning-region-progress")
    public static String LOG_RENDER_PROGRESS = "<gray>(</gray><yellow><percent></yellow><gray>)</gray> <dark_aqua>World<gray>:</gray> <yellow><world></yellow> Chunks<gray>:</gray> <yellow><current_chunks></yellow><gray>/</gray><yellow><total_chunks></yellow> Elapsed<gray>:</gray> <yellow><elapsed></yellow> ETA<gray>:</gray> <yellow><eta></yellow> Rate<gray>:</gray> <yellow><rate></yellow> cps";
    @LangKey("log.scanning-region-progress-with-regions")
    public static String LOG_RENDER_PROGRESS_WITH_REGIONS = "<gray>(</gray><yellow><percent></yellow><gray>)</gray> <dark_aqua>World<gray>:</gray> <yellow><world></yellow> Regions<gray>:</gray> <yellow><current_regions></yellow><gray>/</gray><yellow><total_regions></yellow> Chunks<gray>:</gray> <yellow><current_chunks></yellow><gray>/</gray><yellow><total_chunks></yellow> Elapsed<gray>:</gray> <yellow><elapsed></yellow> ETA<gray>:</gray> <yellow><eta></yellow> Rate<gray>:</gray> <yellow><rate></yellow> cps";

    @LangKey("log.internal-web-disabled")
    public static String LOG_INTERNAL_WEB_DISABLED = "<green>Internal webserver is disabled in config.yml";
    @LangKey("log.internal-web-started")
    public static String LOG_INTERNAL_WEB_STARTED = "<green>Internal webserver running on <bind>:<port>";
    @LangKey("log.internal-web-stopped")
    public static String LOG_INTERNAL_WEB_STOPPED = "<green>Internal webserver stopped";

    // Colorless console log messages
    @LangKey("log.internal-web-start-error")
    public static String LOG_INTERNAL_WEB_START_ERROR = "Internal webserver could not start";
    @LangKey("log.could-not-create-directory")
    public static String LOG_COULD_NOT_CREATE_DIR = "Could not create directory! {path}";
    @LangKey("log.could-not-save-region")
    public static String LOG_COULD_NOT_SAVE_REGION = "Could not save map for region {x},{z}";
    @LangKey("log.internal-web-not-running")
    public static String LOG_INTERNAL_WEB_STOP_ERROR = "An error occurred with the internal webserver";

    private static void init() {
        Arrays.stream(Lang.class.getDeclaredFields())
                .filter(Lang::annotatedString)
                .forEach(Lang::loadValue);
    }

    private static boolean annotatedString(final @NonNull Field field) {
        return field.getType().equals(String.class)
                && field.getDeclaredAnnotation(LangKey.class) != null;
    }

    private static void loadValue(final @NonNull Field field) {
        final LangKey langKey = field.getDeclaredAnnotation(LangKey.class);
        try {
            field.set(null, getString(langKey.value(), (String) field.get(null)));
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load " + Config.LANGUAGE_FILE, e);
        }
    }

    public static void reload() {
        File configFile = plugin.getDataFolder().toPath().resolve(Config.LANGUAGE_FILE).toFile();
        config = new CommentedConfiguration(configFile.toPath());
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load " + Config.LANGUAGE_FILE + ", please correct your syntax errors", ex);
            throw new RuntimeException(ex);
        }
        config.options().copyDefaults(true);

        init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static CommentedConfiguration config;
    private static final Component prefix = parse(PREFIX);

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static @NonNull Component parse(final @NonNull String miniMessage) {
        return MiniMessage.miniMessage().deserialize(miniMessage, Placeholder.unparsed("plugin", plugin.getName()));
    }
    public static @NonNull Component parse(final @NonNull String miniMessage,  @NonNull TagResolver @NonNull ... placeholders) {
        return MiniMessage.miniMessage().deserialize(miniMessage, placeholders);
    }
    public static void send(final @NonNull Audience recipient, final @NonNull String miniMessage) {
        recipient.sendMessage(MiniMessage.miniMessage().deserialize(miniMessage, Placeholder.unparsed("plugin", plugin.getName())));
    }
    public static void send(final @NonNull Audience recipient, final @NonNull Component component) {
        recipient.sendMessage(component);
    }
    public static void sendComponentToConsole(final @NonNull Component component) {
        final Component rendered = GlobalTranslator.render(component, Locale.getDefault());
        plugin.logger.info(prefix.append(rendered));
    }
    public static void sendComponentToConsole(final @NonNull String miniMessage) {
        final Component rendered = parse(miniMessage);
        plugin.logger.info(prefix.append(rendered));
    }

    public static void sendComponentToConsole(final @NonNull String miniMessage, @NonNull Level level ) {
        final Component rendered = parse(miniMessage);
        plugin.getLogger().log(level, MiniMessage.miniMessage().serialize(prefix.append(rendered)));
    }
    public static void sendComponentToConsole(final @NonNull Component component, @NonNull Level level ) {
        final Component rendered = GlobalTranslator.render(component, Locale.getDefault());
        plugin.getLogger().log(level, MiniMessage.miniMessage().serialize(prefix.append(rendered)));
    }
    public Map<String, Object> outputConfig() {
        return config.getValues(true);
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface LangKey {
        @NonNull String value();
    }

}