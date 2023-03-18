/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.romvnly.TownyPlus.configuration.Config;
import me.romvnly.TownyPlus.configuration.Lang;
import me.romvnly.TownyPlus.model.SavedCode;
import me.romvnly.TownyPlus.model.SavedTownData;
import me.romvnly.TownyPlus.util.DatabaseType;
import me.romvnly.TownyPlus.util.Debug;

import org.jetbrains.annotations.Nullable;

import java.sql.*;

public class Database {
    public Connection connection;
    public HikariDataSource ds;
    public String tablePrefix = Config.DB_TABLE_PREFIX;
    public String townTable = tablePrefix + "towns";
    public String codeTable = tablePrefix + "codes";
    public DatabaseType dbType;
    public String dateString = "datetime";
    public Database () throws SQLException {
        reload();
    }
    public void close()  {
        if (ds != null) {
            ds.close();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            reload();
        }
        return connection;
    }
    public HikariDataSource getDataSource() throws SQLException {
        if (ds == null || ds.isClosed()) {
            reload();
        }
        return ds;
    }

    public void initializeDatabase() throws SQLException {
        if (dbType == DatabaseType.POSTGRESQL) {
            dateString = "timestamp";
        }


        Statement statement = getConnection().createStatement();

        //Create the player_stats table

        // I smell a SQL injection here
        // SOMEONE PLEASE FIX THIS ASAP OMG
        String sql = "CREATE TABLE IF NOT EXISTS " + townTable + " (name varchar(36) primary key, discord_server varchar(36), town_chat_id varchar(36), town_chat_webhook_url varchar(1000),nation_chat_id varchar(36), nation_chat_webhook_url varchar(1000), towny_log_channel_id varchar(36), towny_log_webhook_url varchar(1000), towny_info_channel_id varchar(36), towny_info_channel_webhook varchar(1000), town_info_channel_message_id varchar(36), town_discord_roles varchar(4000))";

        statement.execute(sql);

        String sql2 = String.format("CREATE TABLE IF NOT EXISTS " + codeTable + " (code varchar(36) primary key, created_by varchar(36), created_on %s)", dateString);

        statement.execute(sql2);

        statement.close();

    }
    public SavedTownData findTownByName(String name) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+townTable+" WHERE name = ?");
        return getSavedTownData(name, statement);
    }
    public SavedTownData findTownByDiscordServerId(String name) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+townTable+" WHERE discord_server = ?");
        return getSavedTownData(name, statement);
    }
    public SavedCode findCodeByString(String code) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+codeTable+" WHERE code = ?");
        return getSavedCode(code, statement);
    }
    public SavedTownData findTownByTownChatId(String name) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+townTable+" WHERE town_chat_id = ?");
        return getSavedTownData(name, statement);
    }
    public SavedTownData createTownData(SavedTownData savedTownData) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("INSERT INTO "+townTable+" (name, discord_server, town_chat_id, town_chat_webhook_url, nation_chat_id, nation_chat_webhook_url, towny_log_channel_id, towny_log_webhook_url, towny_info_channel_id, towny_info_channel_webhook, town_info_channel_message_id, town_discord_roles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        // name,
        // discord_server,
        // town_chat_id,
        // town_chat_webhook_url,
        // nation_chat_id,
        // nation_chat_webhook_url,
        // towny_log_channel_id,
        // towny_log_webhook_url,
        // towny_info_channel_id,
        // towny_info_channel_webhook,
        // town_info_channel_message_id,
        // town_discord_roles
        statement.setString(1, savedTownData.getName());
        statement.setString(2, savedTownData.getTownDiscordServerID());
        statement.setString(3, savedTownData.getTownChatDiscordID());
        statement.setString(4, savedTownData.getTownChatWebhookURL());
        statement.setString(5, savedTownData.getNationChatDiscordID());
        statement.setString(6, savedTownData.getNationChatWebhookURL());
        statement.setString(7, savedTownData.getTownyLogChannelDiscordID());
        statement.setString(8, savedTownData.getTownyLogChannelWebhookURL());
        statement.setString(9, savedTownData.getTownInfoChannelDiscordID());
        statement.setString(10, savedTownData.getTownInfoChannelWebhookURL());
        statement.setString(11, savedTownData.getTownInfoChannelMessageID());
        statement.setString(12, savedTownData.getTownDiscordRoles());
        statement.execute();
        statement.close();
        return savedTownData;
    }
    public ObjectNode getTownDiscordRoles(String name) throws SQLException, JsonProcessingException {
        PreparedStatement statement = getConnection().prepareStatement("SELECT town_discord_roles FROM "+townTable+" WHERE name = ?");
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            return TownyPlusMain.JSONMapper.readValue(  resultSet.getString("town_discord_roles"), ObjectNode.class);
        }
        return null;
    }
    @Nullable
    private SavedTownData getSavedTownData(String name, PreparedStatement statement) throws SQLException {
        statement.setString(1, name);

        ResultSet resultSet = statement.executeQuery();

        SavedTownData savedTownData;

        if(resultSet.next()){

            savedTownData = new SavedTownData(resultSet.getString("name"), resultSet.getString("discord_server"), resultSet.getString("town_chat_id"), resultSet.getString("town_chat_webhook_url"), resultSet.getString("nation_chat_id"), resultSet.getString("nation_chat_webhook_url"),resultSet.getString("towny_log_channel_id"), resultSet.getString("towny_log_webhook_url"), resultSet.getString("towny_info_channel_id"), resultSet.getString("towny_info_channel_webhook"), resultSet.getString("town_info_channel_message_id"),  resultSet.getString("town_discord_roles"));

            statement.close();

            return savedTownData;
        }

        statement.close();

        return null;
    }
    public String getTownChatChannelId(String name) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("SELECT town_chat_id FROM "+townTable+" WHERE name = ?");
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString("town_chat_id");
        }
        return null;
    }
    @Nullable
    private SavedCode getSavedCode(String code, PreparedStatement statement) throws SQLException {
        statement.setString(1, code);

        ResultSet resultSet = statement.executeQuery();

        SavedCode savedCode;

        if(resultSet.next()){

            savedCode = new SavedCode(resultSet.getString("code"), resultSet.getString("created_by"), resultSet.getDate("created_on"));

            statement.close();

            return savedCode;
        }

        statement.close();

        return null;
    }
    public void createCode(SavedCode savedCode) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("INSERT INTO "+codeTable+" (code, created_by, created_on) VALUES (?, ?, ?)");
        statement.setString(1, savedCode.getCode());
        statement.setString(2, savedCode.getCreatedBy());
        statement.setDate(3, savedCode.getCreatedOn());
        statement.execute();
        statement.close();
    }
    public void deleteCode(SavedCode savedCode) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM "+codeTable+" WHERE code = ?");
        statement.setString(1, savedCode.getCode());
        statement.execute();
        statement.close();
    }
    public void reload() throws SQLException {
        close();

        HikariConfig config = new HikariConfig();
        config.setUsername(Config.DB_USERNAME);
        config.setPassword(Config.DB_PASSWORD);
        // config.setDatabase(Config.DB_NAME);
        config.addDataSourceProperty("useSSL", String.valueOf(Config.DB_USE_SSL));
        config.addDataSourceProperty("requireSSL", String.valueOf(Config.DB_REQUIRE_SSL));
        if (Config.DB_SSL_MODE != null && !Config.DB_SSL_MODE.equalsIgnoreCase("default") && !Config.DB_SSL_MODE.isBlank() && !Config.DB_SSL_MODE.isEmpty()) {
        config.addDataSourceProperty("sslMode", Config.DB_SSL_MODE);
        }
        config.addDataSourceProperty("serverName", Config.DB_HOST);
        config.addDataSourceProperty("port", String.valueOf(Config.DB_PORT));

    
        config.setPoolName("TownyPlusPool");
        config.setConnectionTestQuery("SELECT 1");

        // database is unavariable
        if (Config.DB_TYPE.equalsIgnoreCase("mysql") || Config.DB_TYPE.equalsIgnoreCase("mariadb")) {
            dbType = DatabaseType.MYSQL;
            config.setDriverClassName("com.mysql.jdbc.Driver");
        } else if (Config.DB_TYPE.equalsIgnoreCase("h2")) {
            dbType = DatabaseType.H2;
            config.setDriverClassName(org.h2.Driver.class.getName());
        }
        else if (Config.DB_TYPE.equalsIgnoreCase("sqlite")) {
            dbType = DatabaseType.SQLITE;
            config.setDriverClassName(org.sqlite.JDBC.class.getName());
        }
        else if (Config.DB_TYPE.equalsIgnoreCase("postgres") || Config.DB_TYPE.equalsIgnoreCase("postgressql") || Config.DB_TYPE.equalsIgnoreCase("postgresql")) {
            
            dbType = DatabaseType.POSTGRESQL;
            config.setDriverClassName(org.postgresql.Driver.class.getName());
        }
        else {
            throw new SQLException("Invalid database type");
        }
        if (!Config.DB_URL.equalsIgnoreCase("none") && !Config.DB_URL.equalsIgnoreCase("auto") && !Config.DB_URL.equalsIgnoreCase("default")) {
            config.setJdbcUrl(Config.DB_URL);
            }
            else {
                Debug.log("Setting JDBC URL to: " + "jdbc:" + dbType.toString().toLowerCase() + "://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_NAME);
                config.setJdbcUrl("jdbc:" + dbType.toString().toLowerCase() + "://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_NAME);
            }

        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        config.setLeakDetectionThreshold(60*1000);

        ds = new HikariDataSource(config);
        connection = ds.getConnection();
        TownyPlusMain.getInstance().logger.info(Lang.parse("<green>Database connection established"));
        initializeDatabase();
    }
}
