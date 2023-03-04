/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.command.commands;


import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.MaterialArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.util.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class DiscordCommand extends BaseCommand {

    public DiscordCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.command(this.commandManager.commandBuilder("tgive")
                .senderType(Player.class)
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize("Give yourself stuff"))
                .argument(MaterialArgument.of("material"))
                .argument(IntegerArgument.of("amount"))
                .handler(c -> {
                    final Material material = c.get("material");
                    final int amount = c.get("amount");
                    final ItemStack itemStack = new ItemStack(material, amount);
                    ((Player) c.getSender()).getInventory().addItem(itemStack);
                    c.getSender().sendMessage("You've been given stuff, bro.");
                    c.getSender().sendMessage("Also, this was ran on " + (Bukkit.isPrimaryThread() ? "the main" : "an async") + " thread.");
                }));
        this.commandManager.command(this.commandManager.commandBuilder("tasktest")
                .handler(context -> this.commandManager.taskRecipe()
                        .begin(context)
                        .asynchronous(c -> {
                            c.getSender().sendMessage("ASYNC: " + !Bukkit.isPrimaryThread());
                            return c;
                        })
                        .synchronous(c -> {
                            c.getSender().sendMessage("SYNC: " + Bukkit.isPrimaryThread());
                        })
                        .execute(() -> context.getSender().sendMessage("DONE!"))
                ));
    }

    private void execute(final @NonNull CommandContext<CommandSender> context) {
        Audience sender = plugin.adventure().sender(context.getSender());

    }

}