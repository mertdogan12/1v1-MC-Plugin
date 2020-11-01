package de.mert.Commands;

import de.mert.main.OneVOne;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportWorldCommand implements CommandExecutor {

    /*
     Create a world if it doesn't already exist.
     Then you can join the world.
 */

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, final String[] args) {
        String pr = OneVOne.prefix;
        if (s instanceof Player) {
            final Player p = (Player) s;
            if (p.hasPermission("1v1.build.teleportWorld")) {
                if (args.length == 1) {
                    World w = Bukkit.getWorld(args[0]);
                    if (w != null) {
                        p.teleport(w.getSpawnLocation());
                        p.sendMessage(pr+"Welcome to §6"+w.getName());
                    } else {
                        p.sendMessage(pr+"The world is being created");
                        Bukkit.createWorld(WorldCreator.name(args[0]));
                        p.sendMessage(pr+"The world has been created you can now join it");
                    }
                } else
                    p.sendMessage(pr+"§cUse §4/tpw <World name>");
            } else
                p.sendMessage(pr+"§cNo permissions to perform this command");
        }
        return false;
    }
}
