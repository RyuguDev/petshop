package com.ranull.petshop.listeners;

import com.ranull.petshop.PetShop;
import com.ranull.petshop.manager.PetManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractEntityListener implements Listener {
    private PetShop plugin;
    private PetManager petManager;

    public PlayerInteractEntityListener(PetShop plugin, PetManager petManager) {
        this.plugin = plugin;
        this.petManager = petManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getRightClicked() instanceof Tameable) {
            Player player = event.getPlayer();
            Tameable tameable = (Tameable) event.getRightClicked();

            if (player.getGameMode() != GameMode.SPECTATOR && tameable.getOwner() != null) {
                if (petManager.isForSale(tameable)) {
                    if (tameable.getOwner().equals(player) && player.isSneaking()) {
                        String removeForSale = plugin.getConfig().getString("settings.removeForSale")
                                .replace("$type", petManager.getName(tameable))
                                .replace("&", "§");

                        player.sendMessage(removeForSale);
                        petManager.removeForSale(tameable);
                        event.setCancelled(true);
                    } else if (!tameable.getOwner().equals(player) && player.hasPermission("petshop.buy")) {
                        petManager.transferPet(tameable.getOwner()
                                .getUniqueId(), player, tameable, petManager.getSalePrice(tameable));
                        event.setCancelled(true);
                    }

                    if (player.getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
                        event.setCancelled(true);
                    }
                } else if (tameable.getOwner().equals(player)) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    String saleMaterial = plugin.getConfig().getString("settings.saleMaterial");
                    Material material = Material.getMaterial(saleMaterial);

                    if (material == null) {
                        plugin.getLogger().info("Material ERROR: " + saleMaterial);
                    } else if (itemStack.getType().equals(material)
                            && player.hasPermission("petshop.sell")) {
                        Double price = petManager.getPrice(itemStack);

                        if (price == 0.00) {
                            String invalidName = plugin.getConfig().getString("settings.invalidName")
                                    .replace("&", "§");

                            player.sendMessage(invalidName);
                        } else {
                            petManager.putForSale(tameable, price);

                            if (player.getGameMode() != GameMode.CREATIVE) {
                                itemStack.setAmount(itemStack.getAmount() - 1);
                            }

                            String putForSale = plugin.getConfig().getString("settings.putForSale")
                                    .replace("$type", petManager.getName(tameable))
                                    .replace("&", "§");

                            player.sendMessage(putForSale);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
