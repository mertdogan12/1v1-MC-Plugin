package de.mert.Listener;

import de.mert.vars.MysqlPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public static void on(PlayerQuitEvent e){
        final Player p = e.getPlayer();
        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);
        for (Player p1:
                p.getWorld().getPlayers()) {
            p1.sendMessage("["+p.getName()+"] Elo: §6"+mysqlPlayer.elo.get("All"));
        }

        e.setQuitMessage("");
        if (!p.getWorld().getName().equalsIgnoreCase("world")) {
            if (!PlayerInteractListener.end.get(p.getUniqueId().toString())) {
                for (Player p1:
                        p.getWorld().getPlayers()) {
                    PlayerDeathListener.end1v1(p1 ,PlayerDeathListener.ranked.get(p1.getUniqueId().toString()), p1 != p, PlayerInteractListener.lastKit.get(p.getUniqueId().toString()));
                }
            }
        }

        PlayerInteractListener.unRanked.remove(p);
        PlayerInteractListener.ranked.remove(p);
        PlayerInteractListener.end.remove(p.getUniqueId().toString());
        PlayerInteractListener.pause.remove(p.getUniqueId().toString());
    }
}
