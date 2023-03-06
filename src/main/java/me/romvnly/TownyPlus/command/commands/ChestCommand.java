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

/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.MaterialArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.BaseCommand;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.util.Constants;
import me.romvnly.TownyPlus.util.TimeUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class ChestCommand extends BaseCommand {

    public ChestCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.command(this.commandManager.commandBuilder("tchest")
                .senderType(Player.class)
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize("View your town's chest contents"))
                .permission(Constants.CHEST_PERMISSION)
                .handler(context -> this.commandManager.taskRecipe()
                        .begin(context)
                        .synchronous(c -> {
                            this.execute(c);
                        })
                        .execute(() -> context.getSender().sendMessage("Showing Towny Chest!"))
                ));
    }

    private void execute(final @NonNull CommandContext<CommandSender> context) {
        Audience sender = plugin.adventure().sender(context.getSender());
        Player player = (Player) context.getSender();
        ChestGui gui = new ChestGui(6, "Select amount");

        ItemStack item = new ItemStack(Material.DIAMOND);

        OutlinePane itemPane = new OutlinePane(4, 1, 1, 1);
        itemPane.addItem(new GuiItem(item));

        Label decrement = new Label(2, 1, 1, 1, Font.OAK_PLANKS);
        decrement.setText("-");
        decrement.setVisible(false);

        Label increment = new Label(6, 1, 1, 1, Font.OAK_PLANKS);
        increment.setText("+");

        if (item.getMaxStackSize() == 1) {
            increment.setVisible(false);
        }

        decrement.setOnClick(event -> {
            item.setAmount(item.getAmount() - 1);

            if (item.getAmount() == 1) {
                decrement.setVisible(false);
            }

            increment.setVisible(true);

            gui.update();
        });

        increment.setOnClick(event -> {
            item.setAmount(item.getAmount() + 1);

            decrement.setVisible(true);

            if (item.getAmount() == item.getMaxStackSize()) {
                increment.setVisible(false);
            }

            gui.update();
        });

        gui.addPane(itemPane);
        gui.addPane(decrement);
        gui.addPane(increment);
        gui.show(player);


    }

}