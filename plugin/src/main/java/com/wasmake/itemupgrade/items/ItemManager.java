package com.wasmake.itemupgrade.items;

import com.wasmake.itemupgrade.ItemUpgradePlugin;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemManager {

    private static final NamespacedKey FRAGMENT_KEY = new NamespacedKey("itemupgrade", "fragment");
    private static final NamespacedKey ID_KEY = new NamespacedKey("itemupgrade", "id");
    private static final NamespacedKey LEVEL_KEY = new NamespacedKey("itemupgrade", "level");
    private static final NamespacedKey COST_KEY = new NamespacedKey("itemupgrade", "cost");

    private final ItemUpgradePlugin plugin;

    public ItemManager(final ItemUpgradePlugin plugin) {
        this.plugin = plugin;
    }

    public @Nullable ItemStack fragmentItem() {
        final var item = plugin.config().fragItem();
        return item == null ? null : item.clone();
    }

    public void setFragmentItem(final @NotNull ItemStack item) {
        item.editMeta(meta -> {
            final var dataContainer = meta.getPersistentDataContainer();
            dataContainer.set(FRAGMENT_KEY, PersistentDataType.BYTE_ARRAY, new byte[0]);
        });

        plugin.updateConfig(node -> node.node("frag-item").set(ItemStack.class, item));
    }

    public boolean isFragmentItem(final @NotNull ItemStack item) {
        return item.getItemMeta()
            .getPersistentDataContainer()
            .has(FRAGMENT_KEY, PersistentDataType.BYTE_ARRAY);
    }

    public @NotNull Map<String, Map<String, ItemUpgrade>> items() {
        return plugin.config().items();
    }

    public @NotNull Map<String, ItemUpgrade> upgrades(final int id) {
        return plugin.config().upgrades(id);
    }

    public void setItemUpgrade(
        final @NotNull ItemStack item,
        final int id,
        final int level,
        final int cost
    ) {
        item.editMeta(meta -> {
            final var dataContainer = meta.getPersistentDataContainer();
            dataContainer.set(ID_KEY, PersistentDataType.INTEGER, id);
            dataContainer.set(LEVEL_KEY, PersistentDataType.INTEGER, level);
            dataContainer.set(COST_KEY, PersistentDataType.INTEGER, cost);
        });

        plugin.updateConfig(node -> node
            .node("items")
            .node(id + "")
            .node(level + "")
            .set(ItemUpgrade.class, new ItemUpgrade(cost, item)));
    }

    public boolean removeItemUpgrade(final int id, final int level) {

        final var upgrades = upgrades(id);

        if (upgrades.isEmpty()) {
            return false;
        }

        plugin.updateConfig(node -> node
            .node("items")
            .node(id + "")
            .node(level + "")
            .raw(null));
        return true;
    }


    public @Nullable ItemUpgrade nextUpgrade(final @NotNull ItemStack item) {

        final var id = item.getItemMeta()
            .getPersistentDataContainer()
            .get(ID_KEY, PersistentDataType.INTEGER);

        final var level = item.getItemMeta()
            .getPersistentDataContainer()
            .get(LEVEL_KEY, PersistentDataType.INTEGER);

        if (id == null || level == null) {
            return null;
        }

        final var upgrades = upgrades(id);

        if (upgrades.isEmpty()) {
            return null;
        }

        return upgrades.get(String.valueOf(level + 1));
    }

    public @Nullable ItemUpgrade asItemUpgrade(final @NotNull ItemStack item) {
        final var dataContainer = item.getItemMeta().getPersistentDataContainer();
        final var id = dataContainer.get(ID_KEY, PersistentDataType.INTEGER);
        final var level = dataContainer.get(LEVEL_KEY, PersistentDataType.INTEGER);

        if (id == null || level == null) {
            return null;
        }

        return upgrades(id).get(String.valueOf(level));
    }
    

}
