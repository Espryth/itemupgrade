package com.wasmake.itemupgrade.items;

import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record ItemUpgrade(
    int cost,
    ItemStack item
) { }
