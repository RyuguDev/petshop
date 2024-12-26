package com.ranull.petshop.commands;

import com.ranull.petshop.PetShop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PetShopCommand implements CommandExecutor {
    private PetShop plugin;

    public PetShopCommand(PetShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String version = "1.3";
        String author = "Ranull";

        if (args.length < 1) {
            sender.sendMessage(
                    ChatColor.DARK_GRAY + "» " + ChatColor.GOLD + "PetShop " + ChatColor.GRAY + "v" + version);
            sender.sendMessage(
                    ChatColor.GRAY + "/petshop " + ChatColor.DARK_GRAY + "-" + ChatColor.RESET + " Plugin info");
            if (sender.hasPermission("petshop.reload")) {
                sender.sendMessage(ChatColor.GRAY + "/petshop reload " + ChatColor.DARK_GRAY + "-" + ChatColor.RESET
                        + " Reload plugin");
            }
            sender.sendMessage(ChatColor.DARK_GRAY + "Author: " + ChatColor.GRAY + author);
            return true;
        }
        if (args.length == 1 && args[0].equals("reload")) {
            if (!sender.hasPermission("petshop.reload")) {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "PetShop" + ChatColor.DARK_GRAY + "]"
                        + ChatColor.RESET + " No Permission!");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "PetShop" + ChatColor.DARK_GRAY + "]"
                    + ChatColor.RESET + " Reloaded config file!");
        }
        return true;
    }
}
