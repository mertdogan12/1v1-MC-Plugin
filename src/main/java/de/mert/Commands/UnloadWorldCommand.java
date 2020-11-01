package de.mert.Commands;

import de.mert.main.OneVOne;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

  /*
        Unload a World;
     */

public class UnloadWorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        String pr = OneVOne.prefix;
        Player p = (Player) s;
        if (p.hasPermission("1v1.build.unloadWorld")) {
            if (args.length == 2) {
                if (Bukkit.getWorld(args[0]) != null) {
                    switch (args[1]) {
                        case "t":
                            Bukkit.unloadWorld(args[0], true);
                            break;
                        case "f":
                            Bukkit.unloadWorld(args[0], false);
                            break;
                        default:
                            p.sendMessage(pr+"§cPls use §4/unloadWorld <worldname> <save>");
                            return false;
                    }
                    p.sendMessage(pr+"§6"+args[0]+" §fwurde entfernt");
                } else
                    p.sendMessage(pr+"Welt §c"+args[0]+"§f not exists");
            } else
                p.sendMessage(pr+"§cPls use §4/unloadWorld <worldname> <save>");
        } else
            p.sendMessage(pr+"§cNo permissions to perform this command");
        return false;
    }
}
