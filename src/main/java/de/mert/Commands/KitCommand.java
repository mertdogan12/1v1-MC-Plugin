package de.mert.Commands;

import de.mert.vars.Funktions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static de.mert.main.OneVOne.*;

public class KitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        String pr = prefix;
        if (s instanceof Player) {
            Player p = (Player) s;
            if (p.hasPermission("1v1.changeKit")) {
                if (args.length == 2) {
                    switch (args[0]) {
                        //Shows the Kit
                        case "get":
                            if (getContents(args[1]) != null) {
                                Inventory i = Bukkit.createInventory(null, 9*5);
                                i.setContents(Objects.requireNonNull(getContents(args[1])));
                                for (int integer = 0; integer < Objects.requireNonNull(getArmorContents(args[1])).length; integer++) {
                                    i.setItem(integer+9*4, Objects.requireNonNull(getArmorContents(args[1]))[integer]);
                                }
                                p.openInventory(i);
                            } else
                                p.sendMessage(pr+"§cThe kit doesn't exist");
                            break;

                        //Replaces you Inventory with the Kit
                        case "setInv":
                            if (getContents(args[1]) != null) {
                                p.getInventory().setContents(Objects.requireNonNull(getContents(args[1])));
                                p.getInventory().setArmorContents(getArmorContents(args[1]));
                            } else
                                p.sendMessage(pr+"§cThe kit doesn't exist");
                            break;

                        //save's your Inventory as a kit
                        case "set":
                            try {
                                saveInv(args[1], p.getInventory().getContents(), p.getInventory().getArmorContents());
                            } catch (IOException e) {
                                e.printStackTrace();
                                p.sendMessage(pr+"§cError");
                                return false;
                            }
                            for (Player p1:
                                 p.getWorld().getPlayers()) {
                                p1.getInventory().addItem(Funktions.itembuilder(new ItemStack(Material.DIAMOND), "§6"+args[1], false));
                            }
                            p.sendMessage(pr+"The Kit §6"+args[1]+" is set");
                            break;

                        default:
                            p.sendMessage(pr+"§cPlease use §4/kit [get/set] <name>");
                            break;
                    }
                } else
                    p.sendMessage(pr+"§cPlease use §4/kit [get/set] <name>");
            } else
                p.sendMessage(pr+"§cNo permissions to perform this command");
        }
        return false;
    }
    
    public static void saveInv(String name, ItemStack[] contents, ItemStack[] armor) throws IOException {
        File f = new File("plugins/kits/"+name+"/"+name+".yml");
        if (!f.exists()) {
            File folder = f.getParentFile();
            if (folder != null)  folder.mkdirs();
            f.createNewFile();
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

        c.set(name+".lenght", contents.length);

        //Contents
        for (int it = 0; it < contents.length; it++) {
            if (contents[it] != null) {
                c.set(name+"."+it, contents[it]);
                System.out.println("Item: "+contents[it]);
            } else if (c.isSet(name+"."+it)) {
                c.set(name+"."+it, new ItemStack(Material.AIR));
            }
        }

        //Armor
        c.set(name+".armor.lenght", armor.length);
        for (int it = 0; it < armor.length; it++) {
            if (armor[it] != null) {
                c.set(name+".armor."+it, armor[it]);
                System.out.println("Armor: "+armor[it]);
            } else if (c.isSet(name+".armor."+it)) {
                c.set(name+"."+it, new ItemStack(Material.AIR));
            }
        }

        c.save(f);
    }

    public static ItemStack[] getContents(String name) {
        File f = new File("plugins/kits/"+name+"/"+name+".yml");
        if (!f.exists()) {
            System.out.println("File exestier nicht");
            return null;
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        ItemStack[] i;

        if (c.isSet(name+".lenght")) {

            //Contents
            i = new ItemStack[c.getInt(name+".lenght")];

            for (int it = 0; it <= c.getInt(name+".lenght"); it++) {
                if (c.isSet(name+"."+it)) {
                    i[it] = c.getItemStack(name+"."+it);
                }
            }
            return i;
        } else
            return null;
    }

    public static ItemStack[] getArmorContents (String name) {
        File f = new File("plugins/kits/"+name+"/"+name+".yml");
        if (!f.exists()) {
            System.out.println("File exestier nicht");
            return null;
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        ItemStack[] i;

        if (c.isSet(name+".armor.lenght")) {

            //Contents
            i = new ItemStack[c.getInt(name+".armor.lenght")];

            for (int it = 0; it <= c.getInt(name+".armor.lenght"); it++) {
                if (c.isSet(name+".armor."+it)) {
                    i[it] = c.getItemStack(name+".armor."+it);
                }
            }
            return i;
        } else
            return null;
    }
}
