/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.api;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.romvnly.TownyPlus.TownyPlusMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Gson {
    public static void main(String[] args) {
    }

    public static boolean isJSONValid(String json) {
        try {
            return new JsonParser().parse(json).getAsJsonObject() != null;
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static JsonObject isJSONValid(String json, Boolean getObject) {
        JsonObject result;
        try {
            result = new JsonParser().parse(json).getAsJsonObject();
            return result;
        } catch (Throwable ignored) {
        }
        return null;
    }

    public JsonObject http(String urlString, String body) throws IOException {
        return http(urlString, body, "POST", true);
    }

    public JsonObject http(String urlString, String body, Boolean useCredentials) throws IOException {
        return http(urlString, body, "POST", useCredentials);
    }

    public JsonObject http(String urlString, String body, String method, Boolean useCredentials) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", TownyPlusMain.plugin.config.getString("externalapi.apiKey"));
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        byte[] input = body.getBytes("utf-8");
        os.write(input, 0, input.length);
        TownyPlusMain.plugin.getLogger().info(os.toString());
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String jsonResponseString = response.toString();
            if (isJSONValid(jsonResponseString) == false)
                throw new BindException("The response from the api specified didn't respond with a valid JSON object. Got: " + jsonResponseString);
            TownyPlusMain.plugin.getLogger().info(jsonResponseString);
            JsonParser parser = new JsonParser();
            return parser.parse(response.toString())
                    .getAsJsonObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}