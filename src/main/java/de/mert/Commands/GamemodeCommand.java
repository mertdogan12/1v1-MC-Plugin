package de.mert.Commands;

import de.mert.main.OneVOne;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
    Changes Gamemode
 */

public class GamemodeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        String pr = OneVOne.prefix;

        if (s instanceof Player) {
            Player p = (Player) s;
            if (p.hasPermission("1v1.build.gamemode")) {
                if (args.length == 1) {
                    int gm = 0;
                    try {
                        gm = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                        p.sendMessage(pr+"§cUse §4/gm §l<zahl>");
                        return false;
                    }

                    switch (gm) {
                        case 0:
                            p.setGameMode(GameMode.SURVIVAL);
                            break;
                        case 1:
                            p.setGameMode(GameMode.CREATIVE);
                            break;
                        case 3:
                            p.setGameMode(GameMode.SPECTATOR);
                            break;
                        default:
                            p.sendMessage(pr+"Gamemode: §c§l" + gm + " §fnot available");
                            return false;
                    }
                    p.sendMessage(pr+"Gamemode: §6"+p.getGameMode());
                } else
                    p.sendMessage(pr+"§cUse §4/gm <zahl>");
            } else
                p.sendMessage(pr+"§cNo permissions to perform this command");
        }
        return false;
    }
}
