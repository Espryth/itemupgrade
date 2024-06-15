package com.wasmake.itemupgrade.items;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ItemsConfig {
    @Setting("frag-item")
    private ItemStack fragItem = null;

    private Map<String, Map<String, ItemUpgrade>> items = Map.of();

    public ItemStack fragItem() {
        return fragItem;
    }

    public Map<String, Map<String, ItemUpgrade>> items() {
        return items == null ? Map.of() : items;
    }

    public Map<String, ItemUpgrade> upgrades(final int id) {
        return items == null ? Map.of() : items.getOrDefault(String.valueOf(id), Map.of());
    }

}
