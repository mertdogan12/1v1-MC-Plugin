package de.mert.main;

import de.mert.Commands.*;
import de.mert.Listener.*;
import de.mert.vars.Github;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OneVOne extends JavaPlugin {
    private static OneVOne plugin;
    public static String prefix = "§f[§11§cv§11§f] ";

    @Override
    public void onEnable() {
        ConsoleCommandSender ccs = getServer().getConsoleSender();
        plugin = this;

        // Tests if the Standard Map exists
        try {
            Github github = new Github("Spigot/Standard_Map/");

            if (!github.isDownloaded("Standard_Map/")) {
                new File("Standard_Map").mkdirs();
                github.downloadFiles("Standard_Map/");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tests Connection
        File f1 = new File("plugins/1v1/conf.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f1);

        if (!f1.exists()) {
            f1.getParentFile().mkdirs();
            try {
                f1.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }

        if (!c.isSet("ranked")) {
            c.set("ranked", PlayerJoinListener.rankedMod);
            try {
                c.save(f1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else
            PlayerJoinListener.rankedMod = c.getBoolean("ranked");

        if (PlayerJoinListener.rankedMod) {
            File pwF = new File("plugins/1v1/db.yml");
            if (!pwF.exists()) {
                if (pwF.getParentFile() != null) pwF.getParentFile().mkdirs();
                try {
                    pwF.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            YamlConfiguration db = YamlConfiguration.loadConfiguration(pwF);

            int port = 3306;
            String host = "localhost";
            String database = "minecraft";
            String username = "root";
            String password = "";

            if (db.isSet("pw")) {
                password = db.getString("pw");
            } else db.set("pw", password);

            if (db.isSet("port")) {
                port = db.getInt("port");
            } else db.set("port", port);

            if (db.isSet("ip")) {
                host = db.getString("ip");
            } else db.set("ip", host);

            if (db.isSet("database")) {
                database = db.getString("database");
            } else db.set("database", database);

            if (db.isSet("user")) {
                username = db.getString("user");
            } else db.set("user", username);

            try {
                db.save(pwF);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                synchronized (OneVOne.getPlugin()) {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=latin1", username, password);
                    ccs.sendMessage(prefix + "§6Successfully connected to the Mysql Database");
                    connection.close();
                }
            } catch (ClassNotFoundException | SQLException e) {
                ccs.sendMessage(prefix + "§cNo Connection to the Mysql Server");
                ccs.sendMessage(prefix + "§cDisable the Ranked Mode or correct the Mysql Information");
                ccs.sendMessage(prefix + "§cPlugin is not active");
                return;
            }
        }



        //Commands
        this.getCommand("gm").setExecutor(new GamemodeCommand());
        this.getCommand("tpw").setExecutor(new TeleportWorldCommand());
        this.getCommand("kit").setExecutor(new KitCommand());
        this.getCommand("unload").setExecutor(new UnloadWorldCommand());
        this.getCommand("elo").setExecutor(new GiveEloCommand());
        this.getCommand("c").setExecutor(new CCommand());
        this.getCommand("changeWeather").setExecutor(new ChangeWeatherCommand());

        //Listener
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ArenaListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
        pm.registerEvents(new PlayerInvListener(), this);
        pm.registerEvents(new SettingsListener(), this);
        pm.registerEvents(new SoupListener(), this);
        pm.registerEvents(new ClickPlayerListener(), this);

        //Gamerules
        for (World w:
             Bukkit.getWorlds()) {
            w.setGameRuleValue("doDaylightCycle", "false");
            w.setTime(1000);
            w.setWeatherDuration(0);
        }

        //Holt sich den Author und die Version
        PluginDescriptionFile f = getPlugin().getDescription();
        String authors = f.getAuthors().get(0);
        if (f.getAuthors().size() > 1) {
            authors = "";
            for (String s:
                    f.getAuthors()) {
                authors = authors+s+", ";
            }
        }

        //Startnachricht
        ccs.sendMessage("§1____           ____\n" +
          "                 §1    |  §c\\    /      §1|   §fVersion: §6"+f.getVersion()+"\n" +
          "                 §1    |   §c\\  /       §1|   §fAuthor: §6"+authors+"\n" +
          "                 §1    |    §c\\/        §1|");
    }

    public static OneVOne getPlugin() {
        return plugin;
    }
}
