package me.romvnly.TownyPlus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TownyBypassCommand implements CommandExecutor {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        if (command.getName().equalsIgnoreCase("ae")) {
            if (args.length == 1) {
                return Stream.of("send").filter(a -> a.startsWith(args[0])).collect(Collectors.toList());
            }
            List<String> finalTabCompleter = new ArrayList<>();
            if (sender.hasPermission("announcements.actionbar")) {
                if (args.length == 2) {
                    finalTabCompleter.add("actionbar");
                }
            }
            if (sender.hasPermission("announcements.title")) {
                if (args.length == 2) {
                    finalTabCompleter.add("title");
                }
            }
            if (sender.hasPermission("announcements.broadcast")) {
                if (args.length == 2) {
                    finalTabCompleter.add("broadcast");
                }
            }
            if (sender.hasPermission("announcements.bossbar")) {
                if (args.length == 2) {
                    finalTabCompleter.add("bossbar");
                }
            }
            if (sender.hasPermission("announcements.book")) {
                if (args.length == 2) {
                    finalTabCompleter.add("book");
                }
            }
            if (args.length == 3) {
                // brigadier suggestion: <message>
            }
            return finalTabCompleter.stream().filter(a -> a.startsWith(args[1])).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        commandSender.sendMessage("YOU SUCK! THIS PLGUIN IS MADE BY RTHE EPICS ROMVCNLY!");
        return true;
    }
}
