package com.ranull.petshop.manager;

import com.ranull.petshop.PetShop;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.UUID;

public class PetManager {
    private PetShop plugin;
    private Economy economy;

    public PetManager(PetShop plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    public boolean isForSale(Tameable tameable) {
        for (String tag : tameable.getScoreboardTags()) {
            if (tag.contains("forSale:")) {
                return true;
            }
        }

        return false;
    }

    public String getName(Tameable tameable) {
        return WordUtils.capitalizeFully(tameable.getType().toString()).replace("_", " ");
    }

    public String getSaleTag(Tameable tameable, double price) {
        return plugin.getConfig().getString("settings.saleTag").replace("$type", getName(tameable))
                .replace("$price", String.valueOf(price)).replace("&", "§");
    }

    public void putForSale(Tameable tameable, double price) {
        if (!isForSale(tameable)) {
            tameable.addScoreboardTag("aoeuaoeuauaoe");
            tameable.addScoreboardTag("forSale:" + price);

            if (tameable.getCustomName() != null) {
                tameable.addScoreboardTag("forSaleName:" + tameable.getCustomName()
                        .replace("§", "&"));
            }

            tameable.setCustomName(getSaleTag(tameable, price));
            tameable.setCustomNameVisible(true);
        }
    }

    public void removeForSale(Tameable tameable) {
        if (isForSale(tameable)) {
            Iterator<String> iterator = tameable.getScoreboardTags().iterator();

            while (iterator.hasNext()) {
                String tag = iterator.next();

                if (tag.contains("forSale:")) {
                    tameable.setCustomName(null);
                    tameable.setCustomNameVisible(false);
                    iterator.remove();
                }

                if (tag.contains("forSaleName:")) {
                    tameable.setCustomName(tag.replace("forSaleName:", "").
                            replace("&", "§"));
                    tameable.setCustomNameVisible(false);
                    iterator.remove();
                }
            }
        }
    }

    public double getSalePrice(Tameable tameable) {
        for (String tag : tameable.getScoreboardTags()) {
            if (tag.contains("forSale:")) {
                return Double.parseDouble(tag.replace("forSale:", ""));
            }
        }

        return 0.00;
    }

    public double getPrice(ItemStack itemStack) {
        if (itemStack.getItemMeta().hasDisplayName()) {
            try {
                double price = Double.parseDouble(itemStack.getItemMeta().getDisplayName());

                if (price > 0) {
                    return price;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return 0.00;
    }

    public boolean transferMoney(UUID oldOwner, Player newOwner, double price) {
        double money = economy.getBalance(newOwner);

        if (money >= price) {
            economy.withdrawPlayer(newOwner, price);
            economy.depositPlayer(plugin.getServer().getOfflinePlayer(oldOwner), price);

            return true;
        }

        return false;
    }

    public void transferPet(UUID oldOwner, Player newOwner, Tameable tameable, Double price) {
        if (transferMoney(oldOwner, newOwner, price)) {
            tameable.setOwner(newOwner);
            removeForSale(tameable);

            String soldOldOwner = plugin.getConfig().getString("settings.soldOldOwner").replace("$price", price.toString())
                    .replace("$player", newOwner.getName()).replace("$type", getName(tameable)).replace("&", "§");
            String soldNewOwner = plugin.getConfig().getString("settings.soldNewOwner").replace("$type", getName(tameable))
                    .replace("$price", price.toString()).replace("&", "§");

            if (plugin.getServer().getPlayer(oldOwner) != null) {
                plugin.getServer().getPlayer(oldOwner).sendMessage(soldOldOwner);
            }

            newOwner.sendMessage(soldNewOwner);
        } else {
            String notEnoughMoney = plugin.getConfig().getString("settings.notEnoughMoney")
                    .replace("&", "§");

            newOwner.sendMessage(notEnoughMoney);
        }
    }
}
