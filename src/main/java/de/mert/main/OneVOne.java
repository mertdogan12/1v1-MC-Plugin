package de.mert.main;

import de.mert.Commands.*;
import de.mert.Listener.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.Statement;

public class OneVOne extends JavaPlugin {
    private static OneVOne plugin;
    public static String prefix = "§f[§11§cv§11§f] ";

    @Override
    public void onEnable() {
        ConsoleCommandSender ccs = getServer().getConsoleSender();
        plugin = this;

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
