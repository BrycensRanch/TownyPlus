/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.romvnly.TownyPlus.util.Constants;
import me.romvnly.TownyPlus.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateCommandsTests extends TestBase {

    private PlayerMock player;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
//        player.addAttachment(plugin, Constants.VERSION_PERMISSION, true);
    }

    @Test
    void info_forSelf_printsOwnPlayerName() {
        player.performCommand("townyplus help");
        assertThat(player.nextMessage() != null);
    }
}
