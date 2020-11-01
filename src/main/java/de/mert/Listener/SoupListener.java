package de.mert.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListener implements Listener {
    @EventHandler
    private void on(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (e.getItem() != null) {
            if (!e.getItem().hasItemMeta()) {
                if (a.equals(Action.RIGHT_CLICK_AIR)) {
                    if (e.getItem().equals(new ItemStack(Material.MUSHROOM_SOUP))) {
                        if (p.getHealth() != 20) {
                            double h = p.getHealth() + 3.5;
                            if (h >= 20) h = 20;
                            p.setHealth(h);
                            p.setFoodLevel(20);
                            p.setItemInHand(new ItemStack(Material.BOWL));
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
