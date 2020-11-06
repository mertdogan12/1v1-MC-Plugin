package de.mert.Listener;

import de.mert.main.OneVOne;
import de.mert.vars.Funktions;
import de.mert.vars.MysqlPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static de.mert.vars.Funktions.*;

public class PlayerInvListener implements Listener {
    public static HashMap<String, Inventory> settingsInv = new HashMap<>();

    @EventHandler
    private void on(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getItem() != null) {
                if (e.getItem().hasItemMeta()) {
                    if (e.getItem().getItemMeta().hasDisplayName()) {
                        e.setCancelled(true);
                        
                        String displayName = e.getItem().getItemMeta().getDisplayName();
                        switch (displayName) {
                            case "§6Ranked", "§cUnranked", "§6Elosettings (Matchmaking)":
                                if (!PlayerJoinListener.rankedMod) {
                                    p.sendMessage(OneVOne.prefix+"§cRanked Mode is disabled");
                                    return;
                                }
                        }

                        //Ranked_Settings
                        if (displayName.equalsIgnoreCase("§6Ranked")) {
                            p.getInventory().setItem(7, itembuilder(new ItemStack(Material.ENDER_PEARL), "§cUnranked", false));
                            PlayerInteractListener.modRanked.put(p.getUniqueId().toString(), false);
                        } else if (displayName.equalsIgnoreCase("§cUnranked")) {
                            p.getInventory().setItem(7, itembuilder(new ItemStack(Material.ENDER_PEARL), "§6Ranked", true));
                            PlayerInteractListener.modRanked.put(p.getUniqueId().toString(), true);

                        } else if (displayName.equalsIgnoreCase("§6Elosettings (Matchmaking)")) {
                            //Settings
                            Inventory i = Bukkit.createInventory(null, 9, "§6Elobereich des Gegners");

                            i.setItem(SettingsListener.getPlace((int) mysqlPlayer.mmElo[1]), itembuilder(new ItemStack(Material.REDSTONE_BLOCK), "§c-"+(int) mysqlPlayer.mmElo[1], false));
                            i.setItem(SettingsListener.getPlace((int) mysqlPlayer.mmElo[0]), itembuilder(new ItemStack(Material.GOLD_BLOCK), "§6+"+(int) mysqlPlayer.mmElo[0], true));

                            resetInv(i);

                            settingsInv.put(p.getUniqueId().toString(), i);
                            p.openInventory(i);
                        }
                    }
                }
            }
        }
    }

    public static void resetInv(Inventory i) {
        i.setItem(1, itembuilder(new ItemStack(Material.REDSTONE_BLOCK), "§c-200", false));
        i.setItem(2, itembuilder(new ItemStack(Material.REDSTONE_BLOCK), "§c-100", false));
        i.setItem(3, itembuilder(new ItemStack(Material.REDSTONE_BLOCK), "§c-50", false));
        i.setItem(4, itembuilder(new ItemStack(Material.DIAMOND_BLOCK), "§90", false));
        i.setItem(5, itembuilder(new ItemStack(Material.GOLD_BLOCK), "§6+50", true));
        i.setItem(6, itembuilder(new ItemStack(Material.GOLD_BLOCK), "§6+100", true));
        i.setItem(7, itembuilder(new ItemStack(Material.GOLD_BLOCK), "§6+200", true));
    }
}
