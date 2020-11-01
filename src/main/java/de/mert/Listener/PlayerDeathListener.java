package de.mert.Listener;

import de.mert.main.OneVOne;
import de.mert.vars.Funktions;
import de.mert.vars.MysqlPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerDeathListener implements Listener {
    public static HashMap<String, Location> respawnLoc = new HashMap<>();
    public static HashMap<String, Boolean> ranked = new HashMap<>();
    public static HashMap<String, Double> gegnerElo = new HashMap<>();

    @EventHandler
    private void on(PlayerDeathEvent e) {
        final Player p = e.getEntity();

        e.getDrops().clear();
        e.setDeathMessage("");
        for (Player p1:
             p.getWorld().getPlayers()) {
            end1v1(p1, ranked.get(p1.getUniqueId().toString()), p1 != p, PlayerInteractListener.lastKit.get(p.getUniqueId().toString()));
        }

    }

    @EventHandler
    private void on(PlayerRespawnEvent e) {
        if (respawnLoc.get(e.getPlayer().getName()) != null) {
            if (PlayerInteractListener.end.get(e.getPlayer().getUniqueId().toString())) {
                e.setRespawnLocation(respawnLoc.get(e.getPlayer().getName()));
            } else
                e.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
        }
    }

    public static void end1v1(final Player p, boolean ranked, boolean winner, String kit) {
        String pr = OneVOne.prefix;
        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);

        p.sendMessage(pr+"You will be kicket in §610sec");

        //Save the RespawnLoc and set end = true.
        PlayerInteractListener.end.put(p.getUniqueId().toString(), true);
        respawnLoc.put(p.getName(), p.getLocation());

        if (ranked) {
            //Set the new Elo
            double neueElo;

            System.out.println(kit);
            if (winner) {
                neueElo = elo(mysqlPlayer.elo.get(kit), gegnerElo.get(p.getUniqueId().toString()))[0];
            } else
                neueElo = elo(mysqlPlayer.elo.get(kit), gegnerElo.get(p.getUniqueId().toString()))[1];

            mysqlPlayer.updateElo(neueElo, kit);

            PlayerJoinListener.updateScoreboard(p);
        }
        if (winner) {
            p.sendMessage(pr+"§6You won");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerInteractListener.end.put(p.getUniqueId().toString(), false);
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                p.getInventory().setContents(Funktions.getLobbyKit(PlayerInteractListener.modRanked.get(p.getUniqueId().toString())).getContents());
                p.setHealthScale(20);
                p.setFoodLevel(20);
            }
        }.runTaskLater(OneVOne.getPlugin(), 200);
    }

    public static double[] elo(double w, double l) {
        // 0 = gewinnerElo
        // 1 = verliererElo

        double[] i = new double[2];
        double[] elo = new double[2];

        i[0] = (1 / (1 + Math.pow(10, (l - w) / 400)));
        i[1] = (1 / (1 + Math.pow(10, (w - l) / 400)));

        elo[0] = w + 20 * (1 - i[0]);
        elo[1] = l + 20 * (0 - i[1]);

        return elo;
    }
}

