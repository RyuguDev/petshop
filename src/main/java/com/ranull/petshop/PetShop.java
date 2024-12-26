package com.ranull.petshop;

import com.ranull.petshop.commands.PetShopCommand;
import com.ranull.petshop.hooks.Vault;
import com.ranull.petshop.listeners.PlayerInteractEntityListener;
import com.ranull.petshop.manager.PetManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PetShop extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        Vault vault = new Vault();

        if (vault.setupEconomy()) {
            getCommand("petshop").setExecutor(new PetShopCommand(this));
            getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(this,
                    new PetManager(this, vault.getEconomy())), this);
        } else {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}