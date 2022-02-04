package me.romvnly.TownyPlus.hooks;

import github.scarsz.discordsrv.util.PluginUtil;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

// https://github.com/DiscordSRV/DiscordSRV/blob/master/src/main/java/github/scarsz/discordsrv/hooks/PluginHook.java
// I do not claim ownership of this file, it rightfully belongs to DiscordSRV, which is licensed under GPL v3, just like our plugin.
// View their license here. https://github.com/DiscordSRV/DiscordSRV/blob/master/LICENSE
public interface PluginHook extends Listener {

    default void hook() {
    }

}