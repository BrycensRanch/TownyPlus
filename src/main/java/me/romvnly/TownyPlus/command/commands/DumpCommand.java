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

// anything related to dumping is from GeyserMC, I just modified it to fit my needs
// Kudos and Credits to GeyserMC 

 import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
 import cloud.commandframework.context.CommandContext;
 import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
 import lombok.SneakyThrows;
 import me.romvnly.TownyPlus.TownyPlusMain;
 import me.romvnly.TownyPlus.command.BaseCommand;
 import me.romvnly.TownyPlus.command.CommandManager;
 import me.romvnly.TownyPlus.dump.DumpInfo;
import me.romvnly.TownyPlus.dump.WebUtils;
import me.romvnly.TownyPlus.util.CommandUtil;
 import me.romvnly.TownyPlus.util.Constants;
 import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

 import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
 import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
 import java.net.URL;
 import java.nio.file.Path;
import java.util.Date;
import java.util.List;
 
 public final class DumpCommand extends BaseCommand {
    private static final ObjectMapper MAPPER = new ObjectMapper();


    public DumpCommand(final @NonNull TownyPlusMain plugin, final @NonNull CommandManager commandManager) {
         super(plugin, commandManager);
     }
 
     @Override
     public void register() {
        final var typeOfDumpArgument = StringArgument.<CommandSender>builder("type")
        .asOptionalWithDefault("full")
        .withSuggestionsProvider((context, input) -> List.of("full", "offline"))
        .build();
        final var shouldItUploadServerLogsArgument = BooleanArgument.<CommandSender>builder("shouldUploadServerLogs")
        .asOptional()
        .build();
         this.commandManager.registerSubcommand(builder ->
                 builder.literal("dump").meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize("Dump debug information to somewhere. Like Geyser Dump"))
                        .argument(typeOfDumpArgument, CommandUtil.description("Type of dump to perform"))
                        .argument(shouldItUploadServerLogsArgument, CommandUtil.description("Should it upload server logs?"))
                         .permission(Constants.DUMP_PERMISSION)
                         .handler(this::execute));
     }
 
     @SneakyThrows
     private void execute(final @NonNull CommandContext<CommandSender> context) {
         Audience sender = plugin.adventure().sender(context.getSender());
         String typeOfDump = context.getOrDefault("type", "full");
         Boolean shouldDumpLatestLog = context.getOrDefault("shouldUploadServerLogs", true);

         boolean offlineDump;
         switch (typeOfDump) {
             case "offline" -> offlineDump = true;
             case "full" -> offlineDump = false;
                default -> {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid dump type. Please use <dump> or <dump></red>", Placeholder.unparsed("dump", "full"), Placeholder.unparsed("dump", "offline")));
                    return;
                }
         }

        String dumpData;
        Date date = new Date();
        try {
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            // Make arrays easier to read
            prettyPrinter.indentArraysWith(new DefaultIndenter("    ", "\n"));
            if (offlineDump) {
                dumpData = MAPPER.writer(prettyPrinter).writeValueAsString(new DumpInfo(shouldDumpLatestLog));
            } else {
                dumpData = MAPPER.writer(prettyPrinter).writeValueAsString(new DumpInfo(shouldDumpLatestLog));
            }
        } catch (IOException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred while dumping information. Please check the console for more information.</red>"));
            this.plugin.getLogger().severe("An error occurred while dumping information");
            e.printStackTrace();
            return;
        }
        String uploadedDumpUrl = "";
        if (offlineDump) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Dumping to JSON File..</yellow>"));
            String dumpFileName = String.format("dump-%s.json", date.getTime());
            Path dumpFile = plugin.getDataFolder().toPath().resolve(dumpFileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(dumpFile.toFile());
                outputStream.write(dumpData.getBytes());
                outputStream.close();
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Dump <dump> outputted to the plugins config folder</green>", Placeholder.unparsed("dump", dumpFileName)));
                if (sender != plugin.adventure().console()) {
                    plugin.adventure().console().sendMessage(MiniMessage.miniMessage().deserialize("<green>Dump <dump> outputted to the plugins config folder</green>", Placeholder.unparsed("dump", dumpFileName)));
                }
            } catch (IOException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred while dumping information. Please check the console for more information.</red>"));
                this.plugin.getLogger().severe("An error occurred while dumping information");
                e.printStackTrace();
            }

        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Dumping to Sourcebin...</yellow>"));

            String response;
            JsonNode responseNode;
            try {
                String postBody = MAPPER.writeValueAsString(MAPPER.createObjectNode().put("title", plugin.getName() + " Debug Dump").put("description", "Kudos to GeyserMC for their dump impl").set("files", MAPPER.createArrayNode().add(MAPPER.createObjectNode().put("name", "dump-" + date.getTime() + ".json").put("content", dumpData))));
                response = WebUtils.post(Constants.DUMP_URL + "bins", postBody);
                responseNode = MAPPER.readTree(response);
            } catch (IOException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An error occurred while uploading the dump. Please check the console for more information.</red>"));
                this.plugin.getLogger().severe("An error occurred while dumping information");
                e.printStackTrace();
                return;
            }
            if (!responseNode.has("key")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>The dump service rejected the dump: <error></red>", Placeholder.unparsed("error", responseNode.has("message") ? responseNode.get("message").asText() : response)));
                return;
            }

            URL hostURL = new URL(Constants.DUMP_URL);
            uploadedDumpUrl = hostURL.getProtocol() + "://" + hostURL.getHost() + "/" + responseNode.get("key").asText();
            Component successMessage = MiniMessage.miniMessage().deserialize("<green>Successfully uploaded dump to <url></green>", Placeholder.unparsed("url", uploadedDumpUrl)).clickEvent(ClickEvent.openUrl(uploadedDumpUrl));

            if (sender != plugin.adventure().console()) {
                plugin.adventure().console().sendMessage(successMessage);
            }
            sender.sendMessage(successMessage);
        }

     }
 
 }