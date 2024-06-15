package com.wasmake.itemupgrade.listener;

import com.wasmake.itemupgrade.items.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    private final ItemManager itemManager;

    public ItemListener(final ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onAnvilRename(final PrepareAnvilEvent event) {
        ItemStack item = event.getInventory().getItem(0);
        if (item != null) {
            final var upgrade = itemManager.asItemUpgrade(item);
            if (upgrade != null) {
                event.setResult(null);
            }
        }
    }
}
