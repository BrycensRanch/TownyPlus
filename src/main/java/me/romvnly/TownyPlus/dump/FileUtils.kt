/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.dump;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileUtils {

  /**
   * Read the lines of a file and return it as a stream
   *
   * @param path File path to read
   * @return The lines as a stream
   */
  public static Stream<String> readAllLines(Path path) {
    try {
      return new BufferedReader(
        new InputStreamReader(Files.newInputStream(path))
      )
        .lines();
    } catch (IOException e) {
      throw new RuntimeException("Error while trying to read file!", e);
    }
  }
}
