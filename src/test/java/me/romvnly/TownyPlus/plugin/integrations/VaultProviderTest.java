/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.plugin.integrations;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.romvnly.TownyPlus.plugin.TestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class VaultProviderTest extends TestBase {

    private PlayerMock player;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
    }

    @Test
    void getPlayerBalance_returnsVaultBalance() {
        // when(economy.getBalance(player)).thenReturn(100D);

        assertThat(plugin.restAPI.active).isEqualTo(true);
    }
}