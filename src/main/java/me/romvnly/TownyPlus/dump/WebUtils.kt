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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import javax.naming.directory.InitialDirContext;
import lombok.val;
import me.romvnly.TownyPlus.TownyPlusMain;
import org.eclipse.jetty.server.Response;

public class WebUtils {

  /**
   * Get the string output from the passed {@link HttpURLConnection}
   *
   * @param con The connection to get the string from
   * @return The body of the returned page
   * @throws IOException
   */
  private static String connectionToString(HttpURLConnection con)
    throws IOException {
    // Send the request (we dont use this but its required for getErrorStream() to work)
    con.getResponseCode();

    // Read the error message if there is one if not just read normally
    InputStream inputStream = con.getErrorStream();
    if (inputStream == null) {
      inputStream = con.getInputStream();
    }

    StringBuilder content = new StringBuilder();
    try (
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))
    ) {
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
        content.append("\n");
      }

      con.disconnect();
    }

    return content.toString();
  }

  /**
   * Post a string to the given URL
   *
   * @param reqURL URL to post to
   * @param postContent String data to post
   * @return String returned by the server
   * @throws IOException
   */
  public static String post(String reqURL, String postContent)
    throws IOException {
    URL url = new URL(reqURL);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json; utf-8");
    con.setRequestProperty("Accept", "application/json");
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setChunkedStreamingMode(0);

    // Writes the JSON parsed as string to the connection
    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.write(postContent.toString().getBytes());
    Integer responseCode = con.getResponseCode();

    BufferedReader bufferedReader;

    // Creates a reader buffer
    if (responseCode > 199 && responseCode < 300) {
      bufferedReader =
        new BufferedReader(new InputStreamReader(con.getInputStream()));
    } else {
      bufferedReader =
        new BufferedReader(new InputStreamReader(con.getErrorStream()));
    }

    // To receive the response
    StringBuilder content = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      content.append(line).append("\n");
    }
    bufferedReader.close();

    // Prints the response
    TownyPlusMain.plugin.getLogger().info(content.toString());

    // Check if the connection is successful

    return connectionToString(con);
  }

  /**
   * Post fields to a URL as a form
   *
   * @param reqURL URL to post to
   * @param fields Form data to post
   * @return String returned by the server
   * @throws IOException
   */
  public static String postForm(String reqURL, Map<String, String> fields)
    throws IOException {
    URL url = new URL(reqURL);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    con.setDoOutput(true);

    try (OutputStream out = con.getOutputStream()) {
      // Write the form data to the output
      for (Map.Entry<String, String> field : fields.entrySet()) {
        out.write(
          (
            field.getKey() +
            "=" +
            URLEncoder.encode(
              field.getValue(),
              StandardCharsets.UTF_8.toString()
            ) +
            "&"
          ).getBytes(StandardCharsets.UTF_8)
        );
      }
    }

    return connectionToString(con);
  }
}
