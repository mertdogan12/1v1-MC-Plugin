package de.mert.Commands;

import de.mert.Listener.PlayerJoinListener;
import de.mert.main.OneVOne;
import de.mert.vars.MysqlPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
    Changes the Elo
 */

public class GiveEloCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        MysqlPlayer mysqlPlayer = new MysqlPlayer(p);
        String pr = OneVOne.prefix;
        if (p.hasPermission("1v1.giveElo")) {
            if (args.length == 4) {
                Player e = Bukkit.getPlayer(args[2]);
                if (e.isOnline()) {
                    String kit = args[3];

                    int elo = mysqlPlayer.elo.get(kit);
                    int ammount;
                    int newElo;
                    try {
                        ammount = Integer.parseInt(args[1]);
                    } catch (Exception e1) {
                        p.sendMessage(pr+"§c"+args[1]+" §fis not a number");
                        return false;
                    }
                    switch (args[0]) {
                        case "give":
                            newElo = elo + ammount;
                            break;
                        case "remove":
                            newElo = elo - ammount;
                            break;
                        case "set":
                            newElo = ammount;
                            break;
                        default:
                            p.sendMessage(pr+"§cUse §4/elo §l[give/remove/set] §c<ammount> <player>");
                            return false;
                    }

                    mysqlPlayer.updateElo(newElo, kit);
                    p.sendMessage(pr+"The Elo(Kit: "+kit+") from §6"+e.getName()+" §fis now §1"+newElo);
                    PlayerJoinListener.updateScoreboard(p);
                } else
                    p.sendMessage(pr+"Player §c"+args[2]+" §fis not online");
            } else
                p.sendMessage(pr+"§cUse §4/elo [give/remove/set] <ammount> <player> <kit>");
        } else
            p.sendMessage(pr+"§cNo permissions to perform this command");
        return false;
    }
}
