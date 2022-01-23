package me.romvnly.TownyPlus;

import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class TownyPlusMain extends JavaPlugin {
    public static TownyPlusMain plugin;

    public static TownyPlusMain getInstance() {
        return plugin;
    }

    public void onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException e) {
            getLogger().severe("This plugin requires Paper or one of its forks to run");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
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

        getLogger().info("TownyPlus has been Enabled!");
    }

    public void onDisable() {
        getLogger().info("TownyPlus has been Disabled!");
    }
}