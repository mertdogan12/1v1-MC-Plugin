package de.mert.Listener;

import de.mert.Commands.KitCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ClickPlayerListener implements Listener {
    @EventHandler
    private void on(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked().getType().equals(EntityType.PLAYER)) {
            //Get the name from the kit
            if (!p.getItemInHand().hasItemMeta()) return;
            if (!p.getItemInHand().getItemMeta().hasDisplayName()) return;

            String kit = p.getItemInHand().getItemMeta().getDisplayName().replace("§6", "");

            //If the kit is not available, it will be returned.
            if (KitCommand.getContents(kit) == null) return;
            if (KitCommand.getArmorContents(kit) == null) return;

            p.performCommand("c "+e.getRightClicked().getName()+" "+kit);
        }
    }
}
