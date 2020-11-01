package de.mert.Listener;

import de.mert.vars.Funktions;
import de.mert.vars.MysqlPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;

public class PlayerJoinListener implements Listener {
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

        // Sets the scoreboard
        updateScoreboard(p);
        e.setJoinMessage("");

        // Joinmessage
        for (Player p1:
                p.getWorld().getPlayers()) {
            p1.sendMessage("["+p.getName()+"] Elo: §6"+mysqlPlayer.elo.get("All"));
        }

        p.setFoodLevel(20);
    }

    public static void updateScoreboard(Player p){
        // Updates the Scoreboard
        Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = s.registerNewObjective("asdf", "asdf");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName("§11§cv§11");

        DecimalFormat format = new DecimalFormat("###000.##");
        DecimalFormat format1 = new DecimalFormat("###");

        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);

        double elo = mysqlPlayer.elo.get("Soup") + mysqlPlayer.elo.get("UHC");

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
        o.getScore("§6    ").setScore(9);
        o.getScore("Soup: §6"+format.format(mysqlPlayer.elo.get("Soup"))).setScore(8);
        o.getScore("§6    ").setScore(7);
        o.getScore("UHC: §6"+format.format(mysqlPlayer.elo.get("UHC"))).setScore(6);
        o.getScore("    ").setScore(5);
        o.getScore("Rank:  §6"+rankGroupName).setScore(4);
        o.getScore("   ").setScore(3);
        o.getScore("Money: §6"+100).setScore(2);
        o.getScore("  ").setScore(1);

        p.setScoreboard(s);
    }
}
