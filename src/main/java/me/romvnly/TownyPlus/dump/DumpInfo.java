package me.romvnly.TownyPlus.dump;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.hooks.chat.ChatHook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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
    private final HashInfo hashInfo;
    private final RamInfo ramInfo;
    private LogsInfo logsInfo;
    private final FlagsInfo flagsInfo;
    private BukkitInfo bukkitInfo;

    public DumpInfo(boolean addLog) {
        this.versionInfo = new VersionInfo();

        this.cpuCount = Runtime.getRuntime().availableProcessors();
        this.cpuName = CpuUtils.tryGetProcessorName();
        this.systemLocale = Locale.getDefault();
        this.systemEncoding = System.getProperty("file.encoding");
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

        this.flagsInfo = new FlagsInfo();
        this.bukkitInfo = new BukkitInfo();
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
                this.plugins.add(new PluginInfo(plugin.isEnabled(), plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), plugin.getDescription().getAuthors()));
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
    @AllArgsConstructor
    @Getter
    public static class RESTAPIInfo {
        private final boolean active;
        RESTAPIInfo() {
            active = TownyPlusMain.plugin.restAPI.active;
        }
    }
    @AllArgsConstructor
    @Getter
    public static class ChatHookInfo {
        private final ChatHook chatHook;
        ChatHookInfo() {
            chatHook = TownyPlusMain.plugin.chatHook;
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
    public static class GitInfo {
        private final JsonNode gitProperties; 
    }
}