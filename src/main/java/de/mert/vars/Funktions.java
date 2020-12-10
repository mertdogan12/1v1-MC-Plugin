package de.mert.vars;

import de.mert.main.OneVOne;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Funktions {
    private static final ConsoleCommandSender ccs = OneVOne.getPlugin().getServer().getConsoleSender();

    public static Inventory getLobbyKit(boolean modRanked) {
        Inventory i = Bukkit.createInventory(null, 9*4);


        File f = new File("plugins/kits");
        File[] list = f.listFiles();
        if (f.exists() && list != null) {
            for (File n:
                    list) {

                String kit = n.getName();
                File file = new File(n+"/"+kit+".yml");
                if (!file.exists()) continue;
                YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
                Material m = Material.DIAMOND;

                if (c.isSet("Material")) m = Material.valueOf(c.getString("Material"));

                i.addItem(itembuilder(new ItemStack(m), "§6"+kit, false));
            }
        }
        i.setItem(8, itembuilder(new ItemStack(Material.REDSTONE), "§6Elosettings (Matchmaking)", false));
        if (modRanked) {
            i.setItem(7, itembuilder(new ItemStack(Material.ENDER_PEARL), "§6Ranked", true));
        } else
            i.setItem(7, itembuilder(new ItemStack(Material.ENDER_PEARL), "§cUnranked", false));

        return i;
    }

    public static ItemStack itembuilder(ItemStack itemStack, String name, boolean entchanted) {
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;
        meta.setDisplayName(name);

        if (entchanted) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static void copsFolder(File sourceFolder, File targetFolder) throws IOException {
        if (sourceFolder.exists()) {
            if (sourceFolder.isDirectory()) {
                if (!targetFolder.exists()) {
                    targetFolder.mkdirs();
                    ccs.sendMessage("Dir createt: " + targetFolder);
                }

                String[] files = sourceFolder.list();

                for (String file :
                        files) {
                    File srcFile = new File(sourceFolder, file);
                    File tarFile = new File(targetFolder, file);

                    copsFolder(srcFile, tarFile);
                }
            } else {
                Files.copy(sourceFolder.toPath(), targetFolder.toPath());
                ccs.sendMessage("File Copied to: " + targetFolder);
            }
        } else
            ccs.sendMessage(sourceFolder+" exestier nicht");
    }
    public static void deleteFile(File f) {
        if (f.exists()) {
            if (f.isDirectory()) {
                String[] files = f.list();

                for (String file:
                        files) {
                    File dFile = new File(f, file);
                    deleteFile(dFile);
                }
                f.delete();
                ccs.sendMessage("Deletet Dicetion: "+f);

            } else {
                f.delete();
                ccs.sendMessage("Deletet File: "+f);
            }
        } else
            ccs.sendMessage(f+" exestier nicht");
    }
    public static void setWorld(String w1, String w2) throws IOException {
        deleteFile(new File(w2+"/level.dat"));
        deleteFile(new File(w2+"/level.dat_mcr"));
        deleteFile(new File(w2+"/level.dat_old"));
        deleteFile(new File(w2+"/session.lock"));
        deleteFile(new File(w2+"/region"));

        copsFolder(new File(w1+"/level.dat"), new File(w2+"/level.dat"));
        copsFolder(new File(w1+"/level.dat_mcr"), new File(w2+"/level.dat_mcr") );
        copsFolder(new File(w1+"/level.dat_old"), new File(w2+"/level.dat_old"));
        copsFolder(new File(w1+"/session.lock"), new File(w2+"/session.lock"));
        copsFolder(new File(w1+"/region"), new File(w2+"/region"));
    }
}
