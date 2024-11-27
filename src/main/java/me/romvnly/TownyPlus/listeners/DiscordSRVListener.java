/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.listeners;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ChannelType;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.command.CommandManager;
import me.romvnly.TownyPlus.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class DiscordSRVListener extends ListenerAdapter {
    private TownyPlusMain plugin;
    public DiscordSRVListener(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
        super();
        this.plugin = plugin;
    }
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordReadyEvent(DiscordReadyEvent event) {
        // Example of using JDA's events
        // We need to wait until DiscordSRV has initialized JDA, thus we're doing this inside DiscordReadyEvent
        DiscordUtil.getJda().addEventListener(this);

        // ... we can also do anything other than listen for events with JDA now,
        plugin.logger.info("Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
        // see https://ci.dv8tion.net/job/JDA/javadoc/ for JDA's javadoc
        // see https://github.com/DV8FromTheWorld/JDA/wiki for JDA's wiki
    }
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        plugin.logger.warn("Oh no " + event.getGuild().getName() + " went unavailable :(");
    }
    public void onMessageReceived(@Nonnull final MessageReceivedEvent event) {
//        if (event.getAuthor().isBot()) return;
//        Guild guild = event.getGuild();
//        if (guild == null) return;
//        if (event.getMember() == null) return;
//        if (event.getMember().getUser() == null) return;
//        if (event.getMember().getUser().isBot()) return;
//        if (event.getMember().getUser().isSystem()) return;
        //        if (event.getGuild().getId() != "422627791738109953") return; // testing server
//        if (event.getChannel().getId() != "912487580241645568") return;  // town chat
        if (!event.isFromType(ChannelType.TEXT)) {
            Debug.log(Component.text("Not from type text").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            return;
        }
        if (event.getAuthor().isBot()) {
            Debug.log(Component.text("Author is bot").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            return;
        }
        if (event.getMember() == null) {
            Debug.log(Component.text("Member is null").appendNewline().append(MiniMessage.miniMessage().deserialize("From type <type> <user> <msg>", Placeholder.unparsed("type", event.getChannelType().name()), Placeholder.unparsed("user", event.getAuthor().getAsTag()), Placeholder.unparsed("msg", event.getMessage().getContentDisplay()))));
            return;
        }
        // This checks for codes and generates appropriate channels and messages
        // It was moved to a new class because it was getting too big
        plugin.discordSRVChannelCreator.handleDiscordMessageEvent(event);

    }
}
