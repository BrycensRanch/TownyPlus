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

import static org.junit.jupiter.api.Assertions.assertTrue;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.romvnly.TownyPlus.TownyPlusMain;
// import static org.assertj.core.api.Assertions.assertThat;import io.javalin.Javalin;
// import io.javalin.testtools.JavalinTest;
import org.bukkit.Bukkit;
// import com.google.gson.Gson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
// import me.romvnly.TownyPlus.api.StandardResponse;
// import me.romvnly.TownyPlus.api.StatusResponse;

import org.junit.jupiter.api.Test;

public abstract class TestBase {

  public ServerMock server;
  public TownyPlusMain plugin;

  @BeforeEach
  public void setUp() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(TownyPlusMain.class);
  }

  @Test
  void ensurePluginEnables() {
    assertTrue(plugin.isEnabled());
  }

  // @Test
  // public void GET_to_fetch_users_returns_list_of_users() {
  //     JavalinTest.test(plugin.restAPI.server, (server, client) -> {
  //         assertThat(client.get("/channels").code()).isEqualTo(200);
  //         assertThat(client.get("/").body().string()).isEqualTo(new Gson()
  //         .toJson(new StandardResponse(StatusResponse.SUCCESS, "Root of TownyPlus REST API...")));
  //     });
  // }

  @AfterEach
  public void tearDown() {
    MockBukkit.unmock();
  }
}
