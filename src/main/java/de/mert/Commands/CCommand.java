package de.mert.Commands;

import de.mert.Listener.PlayerDeathListener;
import de.mert.Listener.PlayerInteractListener;
import de.mert.main.OneVOne;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class CCommand implements CommandExecutor {
    public static HashMap<String, String> cc = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        String pr = OneVOne.prefix;
        if (!(s instanceof Player)) {
            s.sendMessage(pr+"§cYou have to be a player to use this command");
            return false;
        }
        Player p = (Player) s;
        String pUuid = p.getUniqueId().toString();

        if (args.length != 2) {
            p.sendMessage(pr+"§cUse §4/c <player> <kit>");
            return false;
        }
        Player e = Bukkit.getPlayer(args[0]);

        //Checks if the player is online
        if (e == null || !e.isOnline()) {
            p.sendMessage(pr+"§cPlayer is not online");
            return false;
        }
        String eUuid = e.getUniqueId().toString();

        //See if there is a kit
        if (KitCommand.getContents(args[1]) == null || KitCommand.getArmorContents(args[1]) == null) {
            p.sendMessage(pr+"§cThe kit doesn't exist");
            return false;
        }

        //Save the kit the player selected
        PlayerInteractListener.lastKit.put(pUuid, args[1]);

        /*
        * If the player has already made a request, both players will respond to each other
        * If not, it is checked whether the player has already made a request to the player (-> yes request is removed again)
        * Otherwise a request will be made to the player
         */
        if (cc.containsKey(eUuid) && cc.get(eUuid).contains(pUuid)) {
            try {
                PlayerInteractListener.unRanked.remove(e);
                PlayerInteractListener.unRanked.remove(p);
                PlayerInteractListener.ranked.remove(p);
                PlayerInteractListener.ranked.remove(e);


                PlayerDeathListener.ranked.put(pUuid, false);
                PlayerDeathListener.ranked.put(eUuid, false);

                PlayerInteractListener.matchmaking(p, e, PlayerInteractListener.lastKit.get(eUuid));
            } catch (IOException ioException) {
                p.sendMessage(pr+"§cError at the matchmaking");
                ioException.printStackTrace();
                return false;
            }
        } else if(cc.containsKey(pUuid) && cc.get(pUuid).contains(eUuid)) {
            cc.remove(pUuid);
            p.sendMessage(pr+"§6"+p.getName()+" §4-/-> §c"+e.getName());
            e.sendMessage(pr+"§6"+p.getName()+" §4-/-> §c"+e.getName());
        }else {
            cc.put(pUuid, eUuid);
            p.sendMessage(pr+"§6"+p.getName()+" §f-§6"+args[1]+"§f-> §c"+e.getName());
            e.sendMessage(pr+"§6"+p.getName()+" §f-§6"+args[1]+"§f-> §c"+e.getName());
        }
        return false;
    }
}
