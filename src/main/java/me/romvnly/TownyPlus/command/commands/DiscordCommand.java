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
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.MaterialArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.model.SavedCode;
import me.romvnly.TownyPlus.model.SavedTownData;
import me.romvnly.TownyPlus.util.Constants;
import me.romvnly.TownyPlus.util.RandomString;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class DiscordCommand extends BaseCommand {

    public DiscordCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        final String description = "<green>Command for setting up the DiscordSRV integration.";
        Component commandDescriptionComponent = MiniMessage.miniMessage().deserialize(description);
        cloud.commandframework.@NonNull CommandManager<CommandSender> command = this.commandManager.command(this.commandManager.commandBuilder("tdiscord", "td", "townydiscord", "towndiscord", "townyplusdiscord")
                .senderType(Player.class)
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, commandDescriptionComponent)
                .handler(this::execute));
        // For testing purposes and for example purposes
        if (Config.DEBUG_MODE || plugin.getDescription().getVersion().contains("SNAPSHOT")) {
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
    }

    private void execute(final @NonNull CommandContext<CommandSender> context) {
        Audience sender = plugin.adventure().sender(context.getSender());
        Player player = ((Player) context.getSender());
        Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
        Town town = resident.getTownOrNull();
        if (town == null) {
            Lang.send(sender, Lang.NOT_IN_TOWN);
            return;
        }
        if (!town.getMayor().getUUID().equals(player.getUniqueId())) {
            Lang.send(sender, Lang.NOT_TOWN_MAYOR);
            return;
        }
        JDA jda = DiscordUtil.getJda();
        String playerDiscordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
        if (playerDiscordId == null) {
            Lang.send(sender, Lang.NOT_LINKED_TO_DISCORD);
            return;
        }
        User discordUser = jda.getUserById(playerDiscordId);
        if (discordUser == null) {
            Lang.send(sender, Lang.NOT_LINKED_TO_DISCORD);
            return;
        }
        SavedTownData townData;
        try {
            townData = plugin.database.findTownByName(town.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if (townData != null) {
            Lang.send(sender, Lang.parse(Lang.TOWN_ALREADY_LINKED, Placeholder.unparsed("town", town.getName())));
            return;
        }
        sender.sendMessage(Component.text("You are linked to the following Discord account: " + discordUser.getAsTag() + " (" + discordUser.getId() + ")"));
        String code = new RandomString(5).nextString();
        Lang.send(sender, Lang.parse(Lang.LINK_DISCORD_TO_TOWN, Placeholder.unparsed("town", town.getName()), Placeholder.unparsed("code", code), Placeholder.unparsed("time", "5 minutes")));
        String inviteString = String.format("https://discord.com/api/oauth2/authorize?client_id=%s&permissions=8&scope=bot+applications.commands", jda.getSelfUser().getId());
        Lang.send(sender, Lang.parse("The bot's invite link is <link>", Placeholder.unparsed("link", inviteString)).clickEvent(ClickEvent.openUrl(inviteString)));
        SavedCode savedCode = new SavedCode(code, player.getUniqueId().toString(), new Date(System.currentTimeMillis()));
        try {
            plugin.database.createCode(savedCode);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create code in database!");
            sender.sendMessage(Lang.parse("<red>No code was created in the database, please contact the server administrator!"));
            e.printStackTrace();
        }


    }

}