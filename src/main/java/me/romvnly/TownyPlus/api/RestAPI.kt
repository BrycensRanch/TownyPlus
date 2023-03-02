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

import static io.javalin.apibuilder.ApiBuilder.*;
import static me.romvnly.TownyPlus.api.Gson.isJSONValid;

// import me.romvnly.TownyPlus.api.controllers.ChannelController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.util.JavalinLogger;
import java.util.function.Supplier;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.api.controllers.ChannelController;
import org.eclipse.jetty.server.Server;

public class RestAPI {

  public final TownyPlusMain plugin;

  public RestAPI(TownyPlusMain plugin) {
    this.plugin = plugin;
  }

  public boolean active = false;
  // public ChannelController channelController;
  public Javalin server;
  public Server jettyServer;

  public void startServer(String host, Integer port) {
    server =
      Javalin.create(config -> {
        // this is the best I can do jetty no longer allows programmatic disabling of their logging
        JavalinLogger.enabled = false;
        config.requestLogger.http((ctx, ms) -> {
          this.plugin.getLogger()
            .info(
              String.format(
                "API Request to %s (%s) from %s %sms %s",
                ctx.path(),
                ctx.method(),
                ctx.ip(),
                ms,
                ctx.statusCode()
              )
            );
        });
      });
    server.before(ctx -> {
      ctx.contentType("application/json");
      JsonObject requestJSONBody = isJSONValid(ctx.body(), true);
      if (
        requestJSONBody == null &&
        (ctx.method().toString() != "GET" && ctx.body().length() == 0)
      ) {
        ctx.status(400);
        ctx.json(
          new Gson()
            .toJson(
              new StandardResponse(
                StatusResponse.ERROR,
                "Couldn't parse your request body as JSON."
              )
            )
        );
      }
      ctx.attribute("body", requestJSONBody);
    });
    server.exception(
      Exception.class,
      (e, ctx) -> {
        plugin.getLogger().warning("The REST API threw a exception!");
        plugin.getLogger().warning(e.toString());
        ctx.status(500);
        ctx.json(
          new Gson()
            .toJson(
              new StandardResponse(
                StatusResponse.ERROR,
                "500 Internal Server Error"
              )
            )
        );
      }
    );
    server.error(
      404,
      ctx -> {
        ctx.result(
          "{\"status\": \"ERROR\", \"message\":\"Page not found. 404!\"}"
        );
      }
    );
    // server.routes(() -> {
    //     crud("channels", new ChannelController(this.plugin, this.server));
    // });
    server.get(
      "/",
      ctx -> {
        ctx.status(200);
        ctx.json(
          new Gson()
            .toJson(
              new StandardResponse(
                StatusResponse.SUCCESS,
                "Root of TownyPlus REST API..."
              )
            )
        );
      }
    );
    server.events(event -> {
      event.serverStarting(() -> {
        this.plugin.getLogger()
          .info(
            String.format("The REST API is starting on port %d", server.port())
          );
      });
      event.serverStartFailed(() -> {
        this.plugin.getLogger()
          .info(String.format("The REST API failed to start"));
      });
      event.serverStopping(() -> {
        this.plugin.getLogger().info("Rest API is stopping...");
      });
      event.serverStopped(() -> {
        this.plugin.getLogger().info("Rest API has stopped.");
      });
    });
    new ChannelController(this.plugin, this.server);
    server.start(host, port);
    plugin
      .getLogger()
      .info(String.format("REST API started on http://%s:%s", host, port));
    this.active = true;
  }

  public void stopServer() {
    server.close();
    this.active = false;
  }
}
