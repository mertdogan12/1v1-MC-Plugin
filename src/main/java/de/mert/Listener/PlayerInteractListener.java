package de.mert.Listener;

import de.mert.Commands.CCommand;
import de.mert.Commands.KitCommand;
import de.mert.main.OneVOne;
import de.mert.vars.Funktions;
import de.mert.vars.MysqlPlayer;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PlayerInteractListener implements Listener {
    public static HashMap<String, Boolean> pause = new HashMap<>();
    public static HashMap<String, Boolean> end = new HashMap<>();
    public static HashMap<String, Boolean> modRanked = new HashMap<>();
    public static HashMap<String, String> playerKit = new HashMap<>();
    public static HashMap<String, String> lastKit = new HashMap<>();
    public static ArrayList<Player> unRanked = new ArrayList<>();
    public static  ArrayList<Player> ranked = new ArrayList<>();
    private static final ConsoleCommandSender ccs = OneVOne.getPlugin().getServer().getConsoleSender();

    @EventHandler
    private void on(PlayerInteractEvent e) throws IOException {
        final Player p = e.getPlayer();
        String kit;
        String pr = OneVOne.prefix;
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            if (e.getItem() != null && e.getItem().hasItemMeta()) {
                if (e.getItem().getItemMeta().hasDisplayName()) {
                    e.setCancelled(true);
                    MysqlPlayer mysqlPlayer = new MysqlPlayer(p);

                    //Get the name from the kit
                    kit = e.getItem().getItemMeta().getDisplayName().replace("§6", "");

                    //If the kit is not available, it will be returned.
                    if (KitCommand.getContents(kit) == null) return;
                    if (KitCommand.getArmorContents(kit) == null) return;

                    if (modRanked.get(p.getUniqueId().toString()) != null) {

                        //See if the player wants to play ranked or not
                        if (!modRanked.get(p.getUniqueId().toString())) {

                            // Unranked
                            // See if the player is already in the queue
                            if (!unRanked.contains(p)) {

                                //Adds the player to the queue, saves the kit from the player
                                unRanked.add(p);
                                playerKit.put(p.getUniqueId().toString(), kit);

                                //See if there are any other players in the queue. If so, they will both be removed from the queue and play against each other
                                for (Player p1:
                                        unRanked) {
                                    if (p1 != p) {
                                        if (playerKit.get(p.getUniqueId().toString()).equalsIgnoreCase(playerKit.get(p1.getUniqueId().toString()))) {
                                            //Saves that the player is playing ranked
                                            unRanked.remove(p1);
                                            unRanked.remove(p);
                                            ranked.remove(p);
                                            ranked.remove(p1);

                                            matchmaking(p1, p, kit);

                                            //Saves that the player is playing ranked
                                            PlayerDeathListener.ranked.put(p.getUniqueId().toString(), false);
                                            PlayerDeathListener.ranked.put(p1.getUniqueId().toString(), false);

                                            //Removed the matschmaking requests to other players
                                            CCommand.cc.remove(p.getUniqueId().toString());
                                            CCommand.cc.remove(p1.getUniqueId().toString());
                                            return;
                                        }
                                    }
                                }
                                p.sendMessage(pr+"§6You have been added to the queue");
                            } else {
                                p.sendMessage(pr+"§cYou have been removed from the queue");
                                unRanked.remove(p);
                            }
                        } else {
                            // Ranked
                            // See if the player is already in the queue
                            if (!ranked.contains(p)) {
                                //Get Min and Max Elo from P1
                                double elo = mysqlPlayer.elo.get(kit);
                                double[] mmElo = new double[2];
                                mmElo[0] = elo+mysqlPlayer.mmElo[0];
                                mmElo[1] = elo-mysqlPlayer.mmElo[1];


                                // Adds the player to the queue, saves the kit from the player
                                ranked.add(p);
                                playerKit.put(p.getUniqueId().toString(), kit);

                                // See if there are any other players in the queue
                                for (Player p2 :
                                        ranked) {
                                    if (p != p2) {
                                        // Get Min and Max Elo from P2
                                        MysqlPlayer mysqlPlayer2 = new MysqlPlayer(p2);
                                        double elo2 = mysqlPlayer2.elo.get(kit);
                                        double[] mmElo2 = new double[2];
                                        mmElo2[0] = elo2+mysqlPlayer2.mmElo[0];
                                        mmElo2[1] = elo2-mysqlPlayer2.mmElo[1];

                                        // See if the values match.
                                        if (playerKit.get(p.getUniqueId().toString()).equalsIgnoreCase(playerKit.get(p2.getUniqueId().toString()))) {
                                            if (mmElo2 != null) {
                                                if (mmElo[0] >= elo2 && mmElo[1] <= elo2) {
                                                    if (mmElo2[0] >= elo && mmElo2[1] <= elo) {
                                                        // Removes the player from the queues
                                                        ranked.remove(p);
                                                        ranked.remove(p2);
                                                        unRanked.remove(p2);
                                                        unRanked.remove(p);

                                                        matchmaking(p, p2, kit);

                                                        // Saves that the player is playing ranked
                                                        PlayerDeathListener.ranked.put(p.getUniqueId().toString(), true);
                                                        PlayerDeathListener.ranked.put(p2.getUniqueId().toString(), true);

                                                        // Stores the opponent's Elo
                                                        PlayerDeathListener.gegnerElo.put(p.getUniqueId().toString(), elo2);
                                                        PlayerDeathListener.gegnerElo.put(p2.getUniqueId().toString(), elo);

                                                        // Stores the opponent's Elo
                                                        CCommand.cc.remove(p.getUniqueId().toString());
                                                        CCommand.cc.remove(p2.getUniqueId().toString());
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                p.sendMessage(pr+"§6Opponents are sought (§c"+mmElo[1]+" §6- §1"+mmElo[0]+"§6)");
                            } else {
                                p.sendMessage(pr+"§cYou have been removed from the queue");
                                ranked.remove(p);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void matchmaking(final Player p1, final Player p2, String kit) throws IOException {
        String pr = OneVOne.prefix;
        p1.sendMessage(pr+"    §6§k||  §6World is sought  §k||");
        p2.sendMessage(pr+"    §6§k||  §6World is sought §k||");
        World w = null;

        // Looking for a free world
        for (int i = 0; i <= 4; i++) {
            String name = String.valueOf(i);
            File f = new File(name);
                if (Bukkit.getWorld(name) == null) Bukkit.createWorld(WorldCreator.name(name));
                w = Bukkit.getWorld(name);
                if (w.getPlayers().size() == 0) {
                    i = 4;
                } else
                    w = null;
        }

        // If no world is found, gymnastics takes place
        if (w == null) {
            ccs.sendMessage(pr+w+" is null");
            return;
        }

        // Reset the map of the world
        if (w.getPlayers().size() > 0) return;
        Bukkit.unloadWorld(w.getName(), false);
        Funktions.setWorld("Standard_Map", w.getName());
        Bukkit.createWorld(WorldCreator.name(w.getName()));


        if (KitCommand.getContents(kit) == null) return;
        if (KitCommand.getArmorContents(kit) == null) return;

        if (kit.equalsIgnoreCase("Soup")) {
            w.setGameRuleValue("naturalRegeneration", "true");
        } else
            w.setGameRuleValue("naturalRegeneration", "false");

        //Teleportier die spieler zur Welt
        Location l1 = new Location(w, -12, 100, 32, 175, 0);
        Location l2 = new Location(w, -12, 100, -12, 0, 0);

        sendToServer(p1, l1, kit);
        sendToServer(p2, l2, kit);
    }

    private static void sendToServer(final Player p, Location location, String kit) {
        //Teleportier die spieler und starte das Spiel
        ccs.sendMessage(kit + " | " + p.getUniqueId().toString());
        lastKit.put(p.getUniqueId().toString(), kit);
        p.getInventory().setContents(Objects.requireNonNull(KitCommand.getContents(kit)));
        p.getInventory().setArmorContents(KitCommand.getArmorContents(kit));
        p.teleport(location);
        ArenaListener.lastLocation.put(p.getUniqueId().toString(), location);
        p.sendMessage("Kit: §6"+kit);
        pause.put(p.getUniqueId().toString(), true);

        p.sendTitle("§6§k||§6 5 §k||", "");
        int k = 5;
        for (int o = 1; o <= 4; o++) {
            k--;
            final int finalK = k;
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.sendTitle("§6§k||§6 "+finalK+" §k||", "");
                }
            }.runTaskLater(OneVOne.getPlugin(), o*20);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                p.sendTitle("§6§k||§6 GO §k||", "");
                pause.put(p.getUniqueId().toString(), false);
            }
        }.runTaskLater(OneVOne.getPlugin(), 5*20);
    }
}

