/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.hooks.chat;

import com.comphenix.protocol.events.PacketContainer;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.util.*;
import java.util.List;
import java.util.stream.Collectors;
import me.romvnly.TownyPlus.TownyPlusMain;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.utilities.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

// https://github.com/DiscordSRV/DiscordSRV/blob/master/src/main/java/github/scarsz/discordsrv/hooks/chat/VentureChatHook.java
// I do not claim ownership of this file, it rightfully belongs to DiscordSRV, which is licensed under GPL v3, just like our plugin.
// View their license here. https://github.com/DiscordSRV/DiscordSRV/blob/master/LICENSE
public class VentureChatHook implements ChatHook {

  @EventHandler(priority = EventPriority.MONITOR)
  public void onVentureChat(VentureChatEvent event) {
    ChatChannel chatChannel = event.getChannel();
    if (chatChannel == null) return; // uh oh, ok then

    String message = ChatColor.stripColor(event.getChat());

    MineverseChatPlayer chatPlayer = event.getMineverseChatPlayer();
    if (chatPlayer == null) return;
    Player player = chatPlayer.getPlayer();
    if (player == null) return;
    // these events are never cancelled
    TownyPlusMain.plugin.processChatMessage(
      player,
      message,
      chatChannel.getName(),
      this
    );
    return;
  }

  @Override
  public void broadcastMessageToChannel(
    String channel,
    Component component,
    Resident resident
  ) {
    ChatChannel chatChannel = ChatChannel.getChannel(channel); // case in-sensitive
    if (chatChannel == null) {
      return;
    }
    String legacy = LegacyComponentSerializer
      .legacySection()
      .serialize(component);
    List<MineverseChatPlayer> playersToNotify = MineverseChat.onlinePlayers
      .stream()
      .filter(p -> p.getListening().contains(chatChannel.getName()))
      .filter(p -> {
        if (channel == "town") {
          Town town = resident.getTownOrNull();
          return (
            town != null && resident.getTownOrNull().hasResident(p.getUUID())
          );
        } else if (channel == "nation") {
          Nation nation = resident.getNationOrNull();
          return (
            nation != null &&
            resident.getNationOrNull().hasResident(p.getUUID().toString())
          );
        }
        return true;
      })
      .filter(p ->
        !chatChannel.hasPermission() ||
        p.getPlayer().hasPermission(chatChannel.getPermission())
      )
      .collect(Collectors.toList());
    for (MineverseChatPlayer player : playersToNotify) {
      String playerMessage = (player.hasFilter() && chatChannel.isFiltered())
        ? Format.FilterChat(legacy)
        : legacy;

      // escape quotes, https://github.com/DiscordSRV/DiscordSRV/issues/754
      playerMessage = playerMessage.replace("\"", "\\\"");
      String json = Format.convertPlainTextToJson(playerMessage, true);
      int hash = (playerMessage.replaceAll("(§([a-z0-9]))", "")).hashCode();
      String finalJSON = Format.formatModerationGUI(
        json,
        player.getPlayer(),
        "Discord",
        chatChannel.getName(),
        hash
      );
      PacketContainer packet = Format.createPacketPlayOutChat(finalJSON);
      Format.sendPacketPlayOutChat(player.getPlayer(), packet);
    }

    PlayerUtil.notifyPlayersOfMentions(
      player ->
        playersToNotify
          .stream()
          .map(MineverseChatPlayer::getPlayer)
          .collect(Collectors.toList())
          .contains(player),
      legacy
    );
  }
}
