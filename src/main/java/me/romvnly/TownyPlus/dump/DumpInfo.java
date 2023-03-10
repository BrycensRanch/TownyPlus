/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.dump;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.palmergames.bukkit.towny.TownyAPI;
import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.romvnly.TownyPlus.TownyPlusMain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.util.CpuUtils;
import me.romvnly.TownyPlus.util.FileUtils;
import me.romvnly.TownyPlus.util.WebUtils;
import me.romvnly.TownyPlus.util.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

@Getter
public class DumpInfo {
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
    public static String getManifestInfo() {
        Enumeration resEnum;
        try {
            URLClassLoader cl = (URLClassLoader) DumpInfo.class.getClassLoader();
            resEnum = cl.getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        String json = TownyPlusMain.getInstance().JSONMapper.writeValueAsString(manifest.getMainAttributes().entrySet());
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
    public DumpInfo(boolean addLog) throws IOException {
        this.versionInfo = new VersionInfo();

        this.cpuCount = Runtime.getRuntime().availableProcessors();
        this.cpuName = CpuUtils.tryGetProcessorName();
        this.systemLocale = Locale.getDefault();
        this.systemEncoding = System.getProperty("file.encoding");
        try {
            this.jarManifestInfo = TownyPlusMain.JSONMapper.readTree(getManifestInfo());
        }
        catch(Exception e ) {
            e.printStackTrace();
            TownyPlusMain.getInstance().getLogger().warning("Unable to get manifest of JAR file");
        }

        File configFile = new File(TownyPlusMain.getInstance().getDataFolder(), "config.yml");
        File langFile = new File(TownyPlusMain.getInstance().getDataFolder(), Config.LANGUAGE_FILE);
        this.configInfo = TownyPlusMain.JSONMapper.readValue(TownyPlusMain.JSONMapper.writeValueAsString(TownyPlusMain.YAMLMapper.readValue(configFile, ObjectNode.class)).replace(Config.DISCORDSRV_WEBHOOK, "[REDACTED]").replace(Config.DB_PASSWORD, "[REDACTED PASSWORD]").replace(Config.DB_URL, "[REDACTED JDBC URL]").replace(Config.githubPAT, "[PAT]"), ObjectNode.class);
        this.localeInfo = TownyPlusMain.YAMLMapper.readValue(langFile, JsonNode.class);
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

            this.gitInfo = TownyPlusMain.JSONMapper.readTree(json).get("git");
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
    @Getter
    public static class BukkitInfo {
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
    public static class PluginInfo {
        public boolean enabled;
        public String name;
        public String version;
        public String main;
        public List<String> authors;
        public String description;
        public String website;
    }
    @Getter
    public static class DatabaseInfo {
        public boolean enabled;
        public String type;
        DatabaseInfo() {
            this.enabled = TownyPlusMain.getInstance().database.connection != null;
            this.type = TownyPlusMain.getInstance().database.dbType.toString().toUpperCase();
        }
    }
    @Getter
    public static class VersionInfo {
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
    public static class LogsInfo {
        private String link;

        public LogsInfo() {
            try {
                Map<String, String> fields = new HashMap<>();
                Path latestLogFilePath = FileSystems.getDefault().getPath("logs", "latest.log");
                fields.put("content", FileUtils.readAllLines(latestLogFilePath).collect(Collectors.joining("\n")));

                JsonNode logData = TownyPlusMain.JSONMapper.readTree(WebUtils.postForm("https://api.mclo.gs/1/log", fields));

                this.link = logData.get("url").textValue();
            } catch (IOException ignored) { }
        }
    }

    @AllArgsConstructor
    @Getter
    public static class HashInfo {
        private final String md5Hash;
        private final String sha256Hash;
        private final String sha512Hash;
    }
    @Getter
    public static class RESTAPIInfo {
        private final boolean active;
        RESTAPIInfo() {
            active = TownyPlusMain.plugin.restAPI.active;
        }
    }
    @Getter
    public static class ChatHookInfo {
        private final Boolean chatHook;
        ChatHookInfo() {
            chatHook = TownyPlusMain.plugin.chatHook != null;
        }
    }

    @Getter
    public static class RamInfo {
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
    public static class FlagsInfo {
        private final List<String> flags;

        FlagsInfo() {
            this.flags = ManagementFactory.getRuntimeMXBean().getInputArguments();
        }
    }
    @Getter
    @AllArgsConstructor
    public static class JarManifestInfo {
        private final JsonNode jarManifestInfo;
    }
    @Getter
    @AllArgsConstructor
    public static class GitInfo {
        private final JsonNode gitProperties; 
    }
}