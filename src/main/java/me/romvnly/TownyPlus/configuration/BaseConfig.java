/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.configuration;

import com.google.common.collect.ImmutableMap;
import me.romvnly.TownyPlus.TownyPlusMain;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "SameParameterValue"})
abstract class BaseConfig {
    final File file;
    final YamlConfiguration yaml;

    BaseConfig(String filename) {
        this.file = new File(TownyPlusMain.getInstance().getDataFolder(), filename);
        this.yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            TownyPlusMain.getInstance().getLogger().severe(String.format("Could not load %s, please correct your syntax errors", filename));
            throw new RuntimeException(ex);
        }
        yaml.options().copyDefaults(true);
    }

    void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex.getCause());
                    } catch (Exception ex) {
                        TownyPlusMain.getInstance().getLogger().severe("Error invoking " + method);
                        ex.printStackTrace();
                    }
                }
            }
        }

        try {
            yaml.save(file);
        } catch (IOException ex) {
            TownyPlusMain.getInstance().getLogger().severe("Could not save " + file);
            ex.printStackTrace();
        }
    }

    void set(String path, Object val) {
        yaml.addDefault(path, val);
        yaml.set(path, val);
    }

    String getString(String path, String def) {
        yaml.addDefault(path, def);
        return yaml.getString(path, yaml.getString(path));
    }

    boolean getBoolean(String path, boolean def) {
        yaml.addDefault(path, def);
        return yaml.getBoolean(path, yaml.getBoolean(path));
    }

    int getInt(String path, int def) {
        yaml.addDefault(path, def);
        return yaml.getInt(path, yaml.getInt(path));
    }

    double getDouble(String path, double def) {
        yaml.addDefault(path, def);
        return yaml.getDouble(path, yaml.getDouble(path));
    }

    <T> List<?> getList(String path, T def) {
        yaml.addDefault(path, def);
        return yaml.getList(path, yaml.getList(path));
    }

    @NonNull <T> Map<String, T> getMap(final @NonNull String path, final @Nullable Map<String, T> def) {
        final ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
        if (def != null && yaml.getConfigurationSection(path) == null) {
//            def.forEach((key, value) -> yaml.addDefault(path + "." + key, value));
            yaml.addDefault(path, def.isEmpty() ? new HashMap<>() : def);
            return def;
        }
        final ConfigurationSection section = yaml.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                @SuppressWarnings("unchecked") final T val = (T) section.get(key);
                if (val != null) {
                    builder.put(key, val);
                }
            }
        }
        return builder.build();
    }

}