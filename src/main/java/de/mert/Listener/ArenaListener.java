package de.mert.Listener;

import de.mert.Commands.ChangeWeatherCommand;
import de.mert.main.OneVOne;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ArenaListener implements Listener {
    public static HashMap<String, Location> lastLocation = new HashMap<>();

    @EventHandler
    private void on(PlayerMoveEvent e) {
        //Makes the players stand still during the initial phase

        Player p = e.getPlayer();
        World w = p.getWorld();

        if (PlayerInteractListener.pause.get(e.getPlayer().getUniqueId().toString()) == null) return;
        if (!PlayerInteractListener.pause.get(e.getPlayer().getUniqueId().toString()))  return;
        if (lastLocation.get(p.getUniqueId().toString()) != null) {
            Location l = lastLocation.get(p.getUniqueId().toString());
            if (!p.getLocation().getBlock().getLocation().equals(new Location(w, l.getX(), l.getY(), l.getZ())))
                p.teleport(new Location(w, l.getX(), l.getY(), l.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
        }
    }

    @EventHandler
    private  void on(WeatherChangeEvent e){
        e.setCancelled(!ChangeWeatherCommand.changeWeather);
    }

    @EventHandler
    private void on(BlockPlaceEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        if (PlayerInteractListener.pause.get(uuid) == null) return;
        if (!PlayerInteractListener.pause.get(uuid))  return;
        if (!PlayerInteractListener.end.get(uuid)) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void on(EntityDamageEvent e) {
        if (e.getEntity().getWorld().getName().equalsIgnoreCase("world")) {
            e.setCancelled(true);
            return;
        }

        String uuid = e.getEntity().getUniqueId().toString();
        if (PlayerInteractListener.pause.get(uuid) == null || PlayerInteractListener.end.get(uuid) == null) return;
        if (PlayerInteractListener.end.get(uuid) || PlayerInteractListener.pause.get(uuid)) e.setCancelled(true);

    }

    @EventHandler
    private void on(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (!p.getWorld().getName().equalsIgnoreCase("world")) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void on(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!p.getWorld().getName().equals("world")) return;
        if (p.hasPermission("1v1.build.dropItem")) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void on(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!p.getWorld().getName().equals("world")) return;
        if (p.hasPermission("1v1.build.clickInventory")) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void on(BlockBreakEvent e) {
        if (e.getPlayer().hasPermission("1v1.build.breakBlock")) return;
        if (e.getBlock().getType() == Material.COBBLESTONE) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void on(FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().equalsIgnoreCase("world")) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void on(PlayerDeathEvent e) {
        final Player p = e.getEntity();

        new BukkitRunnable() {
            @Override
            public void run() {
                p.spigot().respawn();
            }
        }.runTaskLater(OneVOne.getPlugin(), 20*2L);
    }
}
