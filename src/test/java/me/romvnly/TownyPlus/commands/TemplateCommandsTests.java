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
        assertThat(player.nextMessage()).contains("TownyPlus");
    }
}
