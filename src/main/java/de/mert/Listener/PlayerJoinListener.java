package de.mert.Listener;

import de.mert.vars.Funktions;
import de.mert.vars.MysqlPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerJoinListener implements Listener {
    public static boolean rankedMod = false;

    @EventHandler
    private void on(PlayerJoinEvent e) {
        /*
        * See if the player's mysql data is set
        * If not, he sets it
        */
        Player p = e.getPlayer();
        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);

        if (PlayerInteractListener.modRanked.get(p.getUniqueId().toString()) == null)
            PlayerInteractListener.modRanked.put(p.getUniqueId().toString(), false);

        p.getInventory().setContents(Funktions.getLobbyKit(PlayerInteractListener.modRanked.get(p.getUniqueId().toString())).getContents());
        p.teleport(Bukkit.getWorld("world").getSpawnLocation());

        p.setFoodLevel(20);

        File f = new File("plugins/1v1/conf.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

        // Sets the scoreboard
        updateScoreboard(p);
        e.setJoinMessage("");

        // Joinmessage
        for (Player p1:
                p.getWorld().getPlayers()) {
            if (rankedMod) {
                p1.sendMessage("["+p.getName()+"] Elo: §6"+mysqlPlayer.elo.get("All"));
            } else
                p1.sendMessage("§6--> "+p.getName());

        }

    }

    public static void updateScoreboard(Player p){
        if (!rankedMod) return;

        // Updates the Scoreboard
        Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = s.registerNewObjective("asdf", "asdf");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName("§11§cv§11");

        DecimalFormat format = new DecimalFormat("###000.##");
        DecimalFormat format1 = new DecimalFormat("###");

        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);

        double elo = mysqlPlayer.elo.get("All");

        double rank2;
        double rank = elo / 100;
        String rankGroupName;

        if (rank < 3) {
            rank++;
            rankGroupName = "§2Bronze "+format1.format(Math.floor(rank));
        } else if (rank < 6) {
            rank2 = rank - 3;
            rank2++;
            rankGroupName = "§7Silber "+format1.format(rank2);
        } else if (rank < 9) {
            rank2 = Math.floor(rank - 6);
            rank2++;
            rankGroupName = "§bPlatin "+format1.format(rank2);
        } else if (rank < 12) {
            rank2 = rank - 9;
            rank2++;
            rankGroupName = "§6Gold "+format1.format(rank2);
        } else if (rank < 15) {
            rank2 = rank - 12;
            rank2++;
            rankGroupName = "§f§lDiamant "+format1.format(rank2);
        } else {
            rankGroupName = "§c§l§nMeister";
        }


        //Score
        o.getScore("§1    §5").setScore(1);
        o.getScore("Money: §6"+100).setScore(2);
        o.getScore("§3   §5").setScore(3);
        o.getScore("Rank:  §6"+rankGroupName).setScore(4);
        o.getScore("§3   §5").setScore(5);

        AtomicInteger i = new AtomicInteger(1);
        mysqlPlayer.elo.forEach((k, v) -> {
            if (!k.equals("All")) {
                o.getScore(k+": §6"+v).setScore(5+ i.get());
                o.getScore("§3   §5").setScore(5+i.get()+1);
                i.addAndGet(2);
            }
        });

        p.setScoreboard(s);
    }
}
