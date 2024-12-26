package com.ranull.petshop.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    private Economy economy;

    public Economy getEconomy() {
        return economy;
    }

    public boolean setupEconomy() {
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");

        if (vault != null && vault.isEnabled()) {
            RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);

            if (registeredServiceProvider != null) {
                economy = registeredServiceProvider.getProvider();

                return true;
            }
        }

        return false;
    }
}
