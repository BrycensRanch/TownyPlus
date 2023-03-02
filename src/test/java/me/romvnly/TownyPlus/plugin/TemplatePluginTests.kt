/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.plugin;

// import be.seeseemelk.mockbukkit.MockBukkit;
// import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

public class TemplatePluginTests extends TestBase {

  @Test
  public void shouldFirePlayerJoinEvent() {
    server.addPlayer();

    server.getPluginManager().assertEventFired(PlayerJoinEvent.class);
  }
}
