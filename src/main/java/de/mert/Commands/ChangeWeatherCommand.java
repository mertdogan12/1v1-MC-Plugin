package de.mert.Commands;

import de.mert.main.OneVOne;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
    Changes whether you can change the weather
 */


public class ChangeWeatherCommand implements CommandExecutor {
    public static boolean changeWeather = false;

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        String pr = OneVOne.prefix;
        if (p.hasPermission("1v1.build.changeWeather")) {
            if (args.length == 1) {
                switch (args[0]) {
                    case "t", "true":
                        changeWeather = true;
                        break;
                    case "f", "false":
                        changeWeather = false;
                        break;
                    default:
                        p.sendMessage(pr+"§cPls use §4/changeWeather [t,f]");
                        return false;
                }

                p.sendMessage(pr+"Weather: §6"+changeWeather);
            } else
                p.sendMessage(pr+"§cPls use §4/changeWeather [t,f]");
        } else
            p.sendMessage(pr+"§cNo permissions to perform this command");
        return false;
    }
}
