/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) BrycensRanch <https://github.com/BrycensRanch>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author BrycensRanch
 * @author Romvnly
 * @link https://github.com/BrycensRanch/TownyPlus
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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;

// This whole implementation is inspired from https://github.com/GeyserMC/Geyser/blob/master/core/src/main/java/org/geysermc/geyser/command/defaults/VersionCommand.java
public final class ChestCommand extends BaseCommand {

    public ChestCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.command(this.commandManager.commandBuilder("tchest")
                .senderType(Player.class)
                .permission(Constants.CHEST_PERMISSION)
                .handler((context -> {
                    if (!Config.DEBUG_MODE) {
                                    context.sender().sendMessage("The tchest command is disabled as the update to 1.21.x broke the underlying library.");
                                    return;
                    } else {
                        context.sender().sendMessage("<red>The tchest command is broken but continuing execution anyway since DEBUG_MODE ENABLED...");
                    }
                    var runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            Audience sender = plugin.adventure().player(context.sender());
                            Player player = context.sender();
                            ChestGui gui = new ChestGui(6, "Select amount");

                            ItemStack item = new ItemStack(Material.DIAMOND);

                            OutlinePane itemPane = new OutlinePane(4, 1, 1, 1);
                            itemPane.addItem(new GuiItem(item));

                            Label decrement = new Label(2, 1, 1, 1, Font.DIAMOND);
                            decrement.setText("-");
                            decrement.setVisible(false);

                            Label increment = new Label(6, 1, 1, 1, Font.DIAMOND);
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
                    };
                    runnable.runTaskLater(plugin, 20L);
                })));
    }

}