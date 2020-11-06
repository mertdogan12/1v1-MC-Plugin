package de.mert.Listener;

import de.mert.vars.MysqlPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

import static de.mert.vars.Funktions.itembuilder;


public class SettingsListener implements Listener {

    @EventHandler
    private void on(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);
        if (e.getClickedInventory() != null &&
                !e.getClickedInventory().getName().isEmpty()) {
            if (e.getClickedInventory().getName().equalsIgnoreCase("§6Elobereich des Gegners")) {
                ItemStack is = e.getCurrentItem();
                e.setCancelled(true);
                if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    String mmElo;
                    int elo;
                    double newElo;

                    // Gets the number (how much the settings should be changed)
                    String s = is.getItemMeta().getDisplayName();

                    s = s.replace("§6+", "");
                    s = s.replace("§c-", "");


                    try {
                        elo = Integer.parseInt(s);
                    } catch (Exception e1) {
                        return;
                    }

                    // Change the numbers
                    if (elo < 0) elo = 0;

                    Inventory i = PlayerInvListener.settingsInv.get(p.getUniqueId().toString());

                    int place = 0;

                    if (elo == 0) return;

                    PlayerInvListener.resetInv(i);

                    // See if the min or max Elo is switched
                    if (is.getEnchantments().isEmpty()) {
                        mmElo = "settings_minElo";
                        i.setItem(getPlace(elo), itembuilder(new ItemStack(Material.REDSTONE_BLOCK), "§c-"+elo, true));
                        i.setItem(8-getPlace((int) mysqlPlayer.mmElo[0]), itembuilder(new ItemStack(Material.GOLD_BLOCK), "§6+"+mysqlPlayer.mmElo[0], false));
                    } else {
                        mmElo = "settings_maxElo";
                        i.setItem(8-getPlace(elo), itembuilder(new ItemStack(Material.GOLD_BLOCK), "§6+"+elo, false));
                        i.setItem(getPlace((int) mysqlPlayer.mmElo[1]), itembuilder(new ItemStack(Material.REDSTONE_BLOCK), "§c-"+(int) mysqlPlayer.mmElo[1], true));
                    }

                    mysqlPlayer.updatemmElo(elo, mmElo);
                }
            }
        }
    }

    public static int getPlace(int elo) {
        switch (elo) {
            case 200:
                return 1;
            case 100:
                return 2;
            case 50:
                return 3;
            default:
                return 0;
        }
    }
}
