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

package me.romvnly.TownyPlus.command.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

// anything related to dumping is from GeyserMC, I just modified it to fit my needs
// Kudos and Credits to GeyserMC

import lombok.SneakyThrows;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.util.WebUtils;
import me.romvnly.TownyPlus.util.CommandUtil;
import me.romvnly.TownyPlus.util.Constants;
import me.romvnly.TownyPlus.util.CpuUtils;
import me.romvnly.TownyPlus.util.FileUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import org.incendo.cloud.annotations.descriptor.ArgumentDescriptor;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;


public final class DumpCommand extends BaseCommand {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Getter
    public static ObjectMapper YAMLMapper;
    @Getter
    public static ObjectMapper JSONMapper;

    public DumpCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("dump")
                        .optional("type", stringParser(), DefaultValue.constant("full"), SuggestionProvider.suggestingStrings("full", "offline"))
                        .optional("shouldUploadServerLogs", booleanParser(), DefaultValue.constant(true), SuggestionProvider.suggestingStrings("true", "false"))
                        .permission(Constants.DUMP_PERMISSION)
                        .handler((context) -> {
                            if (!Config.DEBUG_MODE) {
                                context.sender().sendMessage("The dump command is disabled. For now, create bug reports manually with information you deem relevant ie Java version and Minecraft server version for example `Java 21 Purpur 1.21.3`");
                                return;
                            }
                            JSONMapper = new ObjectMapper()
                            .enable(JsonParser.Feature.IGNORE_UNDEFINED)
                            .enable(JsonParser.Feature.ALLOW_COMMENTS)
                            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
                            YAMLMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLParser.Feature.EMPTY_STRING_AS_NULL).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
                            var runnable = new BukkitRunnable() {
                                @SneakyThrows
                                @Override
                                public void run() {
                                    Audience sender = plugin.adventure().sender(context.sender());
                                    ComponentLogger logger = plugin.logger;
                                    String typeOfDump = context.getOrDefault("type", "full");
                                    Boolean shouldDumpLatestLog = context.getOrDefault("shouldUploadServerLogs", true);

                                    boolean offlineDump;
                                    switch (typeOfDump) {
                                        case "offline" -> offlineDump = true;
                                        case "full" -> offlineDump = false;
                                        default -> {
                                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid dump type. Please use full or offline</red>"));
                                            return;
                                        }
                                    }

                                    String dumpData;
                                    Date date = new Date();
                                    try {
                                        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
                                        // Make arrays easier to read
                                        prettyPrinter.indentArraysWith(new DefaultIndenter("    ", "\n"));
                                        dumpData = MAPPER.writer(prettyPrinter).writeValueAsString(new DumpInfo(shouldDumpLatestLog, JSONMapper, YAMLMapper));
                                    } catch (IOException e) {
                                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred while dumping information. Please check the console for more information.</red>"));
                                        logger.error("An error occurred while dumping information");
                                        e.printStackTrace();
                                        return;
                                    }
                                    logger.trace("WARNING, I AM ABOUT TO LOG A MASSSSSSIVE FILE");
                                    logger.trace(dumpData);
                                    String uploadedDumpUrl = "";
                                    if (offlineDump) {
                                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Dumping to JSON File..</yellow>"));
                                        String dumpFileName = String.format("dump-%s.json", date.getTime());
                                        Path dumpFile = plugin.getDataFolder().toPath().resolve(dumpFileName);
                                        try {
                                            FileOutputStream outputStream = new FileOutputStream(dumpFile.toFile());
                                            outputStream.write(dumpData.getBytes());
                                            outputStream.close();
                                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Dump <dump> outputted to the plugins config folder</green>", Placeholder.unparsed("dump", dumpFileName)));
                                            if (sender != plugin.adventure().console()) {
                                                plugin.adventure().console().sendMessage(MiniMessage.miniMessage().deserialize("<green>Dump <dump> outputted to the plugins config folder</green>", Placeholder.unparsed("dump", dumpFileName)));
                                            }
                                        } catch (IOException e) {
                                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred while dumping information. Please check the console for more information.</red>"));
                                            logger.error("An error occurred while dumping information");
                                            e.printStackTrace();
                                        }
                                        return;
                                    }

                                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Dumping to Sourcebin...</yellow>"));

                                    String response;
                                    JsonNode responseNode;
                                    try {
                                        String postBody = MAPPER.writeValueAsString(MAPPER.createObjectNode().put("title", plugin.getName() + " Debug Dump").put("description", "Kudos to GeyserMC for their dump impl").set("files", MAPPER.createArrayNode().add(MAPPER.createObjectNode().put("name", "dump-" + date.getTime() + ".json").put("content", dumpData))));
                                        response = WebUtils.post(Constants.DUMP_URL + "bins", postBody);
                                        responseNode = MAPPER.readTree(response);
                                    } catch (IOException e) {
                                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred while uploading the dump. Please check the console for more information.</red>"));
                                        logger.error("An error occurred while dumping information");
                                        e.printStackTrace();
                                        return;
                                    }
                                    if (!responseNode.has("key")) {
                                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>The dump service rejected the dump: <error></red>", Placeholder.unparsed("error", responseNode.has("message") ? responseNode.get("message").asText() : response)));
                                        return;
                                    }

                                    URL hostURL = new URL(Constants.DUMP_URL);
                                    uploadedDumpUrl = hostURL.getProtocol() + "://" + hostURL.getHost() + "/" + responseNode.get("key").asText();
                                    Component successMessage = MiniMessage.miniMessage().deserialize("<green>Successfully uploaded dump to <url></green>", Placeholder.unparsed("url", uploadedDumpUrl)).clickEvent(ClickEvent.openUrl(uploadedDumpUrl));

                                    if (sender != plugin.adventure().console()) {
                                        plugin.adventure().console().sendMessage(successMessage);
                                    }
                                    sender.sendMessage(successMessage);
                                }
                            };
                            runnable.runTaskLater(plugin, 20L);

                        }));
    }

}


@Getter
final class DumpInfo {
    @JsonIgnore
    private static final long MEGABYTE = 1024L * 1024L;

    private final DumpInfo.VersionInfo versionInfo;
    private final int cpuCount;
    private final String cpuName;
    private final Locale systemLocale;
    private final String systemEncoding;
    private final JsonNode gitInfo;
    private JsonNode jarManifestInfo = null;
    private final HashInfo hashInfo;
    private final RamInfo ramInfo;
    private LogsInfo logsInfo;
    private final FlagsInfo flagsInfo;
    private final RESTAPIInfo restAPIInfo;
    private final ChatHookInfo chatHookInfo;
    private BukkitInfo bukkitInfo;
    private ObjectNode configInfo;
    private ObjectNode townyInfo;
    private JsonNode localeInfo;
    private DatabaseInfo databaseInfo;
    public DumpInfo(boolean addLog, ObjectMapper JSONMapper, ObjectMapper YAMLMapper) throws IOException {
        this.versionInfo = new VersionInfo();

        this.cpuCount = Runtime.getRuntime().availableProcessors();
        this.cpuName = CpuUtils.tryGetProcessorName();
        this.systemLocale = Locale.getDefault();
        this.systemEncoding = System.getProperty("file.encoding");
        try {
            this.jarManifestInfo = JSONMapper.readTree(getManifestInfo());
        }
        catch(Exception e ) {
            e.printStackTrace();
            TownyPlusMain.getInstance().getLogger().warning("Unable to get manifest of JAR file");
        }

        File configFile = new File(TownyPlusMain.getInstance().getDataFolder(), "config.yml");
        File langFile = new File(TownyPlusMain.getInstance().getDataFolder(), Config.LANGUAGE_FILE);
        this.configInfo = JSONMapper.readValue(JSONMapper.writeValueAsString(YAMLMapper.readValue(configFile, ObjectNode.class)).replace(Config.DISCORDSRV_WEBHOOK, "[REDACTED]").replace(Config.DB_PASSWORD, "[REDACTED PASSWORD]").replace(Config.DB_URL, "[REDACTED JDBC URL]").replace(Config.githubPAT, "[PAT]"), ObjectNode.class);
        this.localeInfo = YAMLMapper.readValue(langFile, JsonNode.class);
        // Bad idea in the first place
//        this.townyInfo = TownyPlusMain.JSONMapper.createObjectNode()
//                .set("nations", TownyPlusMain.JSONMapper.valueToTree(TownyAPI.getInstance().getNations()));
//        townyInfo.set("towns", TownyPlusMain.JSONMapper.valueToTree(TownyAPI.getInstance().getTowns()));

//        townyInfo.set("residents", TownyPlusMain.JSONMapper.valueToTree(TownyAPI.getInstance().getResidents()));
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("git.properties")) {
            Properties gitProp = new Properties();
            gitProp.load(stream);
            JavaPropsMapper javaPropsMapper = JavaPropsMapper.builder().build();
             String json = javaPropsMapper.readPropertiesAs(gitProp, JsonNode.class).toPrettyString();

            this.gitInfo = JSONMapper.readTree(json).get("git");
        } catch (IOException e) {
            throw new RuntimeException("Unable to load git.properties", e);
        }
        String md5Hash = "unknown";
        String sha256Hash = "unknown";
        String sha512Hash = "unknown";
        try {
            // https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
            // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
            File file = new File(DumpInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            ByteSource byteSource = Files.asByteSource(file);
            md5Hash = byteSource.hash(Hashing.md5()).toString();
            sha256Hash = byteSource.hash(Hashing.sha256()).toString();
            sha512Hash = byteSource.hash(Hashing.sha512()).toString();
        } catch (Exception e) {
            TownyPlusMain.getInstance().getLogger().warning("Unable to get hash of JAR file");
            e.printStackTrace();
        }
        this.hashInfo = new HashInfo(md5Hash, sha256Hash, sha512Hash);

        this.ramInfo = new DumpInfo.RamInfo();

        if (addLog) {
            this.logsInfo = new LogsInfo();
        }
        this.chatHookInfo = new ChatHookInfo();
        this.restAPIInfo = new RESTAPIInfo();
        this.flagsInfo = new FlagsInfo();
        this.bukkitInfo = new BukkitInfo();
        this.databaseInfo = new DatabaseInfo();

    }
    public String getManifestInfo() {
        Enumeration resEnum;
        try {
            URLClassLoader cl = (URLClassLoader) DumpCommand.class.getClassLoader();
            resEnum = cl.getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        String json = new ObjectMapper()
                        .enable(JsonParser.Feature.IGNORE_UNDEFINED)
                        .enable(JsonParser.Feature.ALLOW_COMMENTS)
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                        .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                        .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES).writeValueAsString(manifest.getMainAttributes().entrySet());
                        return json;
                    }
                }
                catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
        } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
        }
        return null;
    }
    @Getter
    public class BukkitInfo {
        private final String platformName;
        private final String platformVersion;
        private final String platformAPIVersion;
        private final boolean onlineMode;

        private final String serverIP;
        private final int serverPort;
        private final List<PluginInfo> plugins;

        BukkitInfo() {
            this.platformName = Bukkit.getName();
            this.platformVersion = Bukkit.getVersion();
            this.platformAPIVersion = Bukkit.getBukkitVersion();
            this.onlineMode = Bukkit.getOnlineMode();
            this.serverIP = Bukkit.getIp();
            this.serverPort = Bukkit.getPort();
            this.plugins = new ArrayList<>();

            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                this.plugins.add(new PluginInfo(plugin.isEnabled(), plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), plugin.getDescription().getAuthors(), plugin.getDescription().getDescription(), plugin.getDescription().getWebsite()));
            }
        }
    }
    @Getter
    @AllArgsConstructor
    public class PluginInfo {
        public boolean enabled;
        public String name;
        public String version;
        public String main;
        public List<String> authors;
        public String description;
        public String website;
    }
    @Getter
    public class DatabaseInfo {
        public boolean enabled;
        public String type;
        DatabaseInfo() {
            this.enabled = TownyPlusMain.getInstance().database.connection != null;
            this.type = TownyPlusMain.getInstance().database.dbType.toString().toUpperCase();
        }
    }
    @Getter
    public class VersionInfo {
        private final String name;
        private final String version;
        private final String javaName;
        private final String javaVendor;
        private final String javaVersion;
        private final String architecture;
        private final String operatingSystem;
        private final String operatingSystemVersion;

        VersionInfo() {
            this.name = TownyPlusMain.plugin.getName();
            this.version = TownyPlusMain.plugin.getDescription().getVersion();
            this.javaName = System.getProperty("java.vm.name");
            this.javaVendor = System.getProperty("java.vendor");
            this.javaVersion = ManagementFactory.getRuntimeMXBean().getVmVersion(); // Gives a little more to the version we can use over the system property
            // Usually gives Java architecture but still may be helpful.
            this.architecture = System.getProperty("os.arch");
            this.operatingSystem = System.getProperty("os.name");
            this.operatingSystemVersion = System.getProperty("os.version");
        }
    }
    @Getter
    public class LogsInfo {
        private String link;

        public LogsInfo() {
            try {
                Map<String, String> fields = new HashMap<>();
                Path latestLogFilePath = FileSystems.getDefault().getPath("logs", "latest.log");
                fields.put("content", FileUtils.readAllLines(latestLogFilePath).collect(Collectors.joining("\n")));

                JsonNode logData = new ObjectMapper()
                .enable(JsonParser.Feature.IGNORE_UNDEFINED)
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES).readTree(WebUtils.postForm("https://api.mclo.gs/1/log", fields));

                this.link = logData.get("url").textValue();
            } catch (IOException ignored) { }
        }
    }

    @AllArgsConstructor
    @Getter
    public class HashInfo {
        private final String md5Hash;
        private final String sha256Hash;
        private final String sha512Hash;
    }
    @Getter
    public class RESTAPIInfo {
        private final boolean active;
        RESTAPIInfo() {
            active = TownyPlusMain.plugin.restAPI.active;
        }
    }
    @Getter
    public class ChatHookInfo {
        private final Boolean chatHook;
        ChatHookInfo() {
            chatHook = TownyPlusMain.plugin.chatHook != null;
        }
    }

    @Getter
    public class RamInfo {
        private final long free;
        private final long total;
        private final long max;

        RamInfo() {
            this.free = Runtime.getRuntime().freeMemory() / MEGABYTE;
            this.total = Runtime.getRuntime().totalMemory() / MEGABYTE;
            this.max = Runtime.getRuntime().maxMemory() / MEGABYTE;
        }
    }

    /**
     * E.G. `-Xmx1024M` - all runtime JVM flags on this machine
     */
    @Getter
    public class FlagsInfo {
        private final List<String> flags;

        FlagsInfo() {
            this.flags = ManagementFactory.getRuntimeMXBean().getInputArguments();
        }
    }
    @Getter
    @AllArgsConstructor
    public class JarManifestInfo {
        private final JsonNode jarManifestInfo;
    }
    @Getter
    @AllArgsConstructor
    public class GitInfo {
        private final JsonNode gitProperties;
    }
}
