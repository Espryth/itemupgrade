package com.wasmake.itemupgrade.cmd;

import com.wasmake.itemupgrade.command.AbstractCommand;
import com.wasmake.itemupgrade.command.api.annotation.Command;
import com.wasmake.itemupgrade.command.api.annotation.OptArg;
import com.wasmake.itemupgrade.command.api.annotation.Sender;
import com.wasmake.itemupgrade.items.ItemManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ItemUpgradeCmd extends AbstractCommand {

    private final ItemManager itemManager;

    public ItemUpgradeCmd(final ItemManager itemManager) {
        super("itemupgrade", "iu");
        this.itemManager = itemManager;
    }


    @Command(name = "setfrag", desc = "Set frag item")
    public void setFrag(final @Sender Player sender) {
        // Check if is OP
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        final var item = sender.getInventory().getItemInMainHand().clone();

        if (item.getType().isAir()) {
            sender.sendMessage("§cYou need to hold an item in your hand.");
            return;
        }

        itemManager.setFragmentItem(item);
        sender.sendMessage("§aFrag item set.");
    }

    @Command(name = "getfrag", desc = "Get frag item")
    public void getFrag(final @Sender Player sender, final int amount) {
        // Check if is OP
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        final var fragItem = itemManager.fragmentItem();

        if (fragItem == null) {
            sender.sendMessage("§cFrag item not set.");
            return;
        }

        fragItem.setAmount(amount);
        sender.getInventory().addItem(fragItem);

        sender.sendMessage("§aFrag item delivered to " + sender.getName());
    }

    @Command(name = "set", desc = "Set an item to the upgrade menu")
    public void set(final @Sender Player sender, final int id, final int level, final int cost) {

        // Check if is OP
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        // Get item in hand
        final var item = sender.getInventory().getItemInMainHand().clone();
        itemManager.setItemUpgrade(item, id, level ,cost);
        sender.sendMessage("§aItem added to the upgrade menu.");
    }

    @Command(name = "remove", desc = "Removes an item from the upgrade menu")
    public void remove(final @Sender Player sender, final int id, final int level) {
        // Check if is OP
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        if (itemManager.removeItemUpgrade(id, level)) {
            sender.sendMessage("§aItem removed from the upgrade menu.");
        } else {
            sender.sendMessage("§cCan't remove the item.");
        }
    }

    @Command(name = "give", desc = "Give an item to the player")
    public void executeGive(@Sender Player player, int index, int level, @OptArg Player argPlayer) {

        if (argPlayer != null) {
            player = argPlayer;
        }

        final var upgrades = itemManager.upgrades(index);
        final var itemUpgrade = upgrades.get(level);

        if (itemUpgrade != null) {
            player.getInventory().addItem(itemUpgrade.item());
            player.sendMessage("§aItem delivered to " + player.getName());
            return;
        }

        player.sendMessage("§cCan't get the item.");
    }

    @Command(name = "show", desc = "Display a menu with all the items to the player")
    public void show(@Sender Player player) {
        // Show the items menu
        final var items = itemManager.items();

        // Create a Bukkit inventory
        Inventory inventory = Bukkit.createInventory(player, 54, Component.text("Item Upgrade Menu"));

        for (final var entry : items.entrySet()) {
            for (final var upgrade : entry.getValue().values()) {
                inventory.addItem(upgrade.item());
            }
        }

        player.openInventory(inventory);
        player.sendMessage("§aItem upgrade menu opened.");
    }

    @Command(name = "upgrade", desc = "Upgrade the item in your hand")
    public void upgrade(@Sender Player player) {
        // Upgrade item
        final var itemStack = player.getInventory().getItemInMainHand();
        final var nextUpgrade = itemManager.nextUpgrade(itemStack);

        if (nextUpgrade == null) {
            player.sendMessage("§cCan't get an upgrade for this item.");
            return;
        }

        // Check if the player has the required frags to upgrade the item in the inventory
        final var fragItem = itemManager.fragmentItem();

        if (fragItem != null) {
            var cost = nextUpgrade.cost();
            var amount = 0;

            for (final var item : player.getInventory().getContents()) {
                if (item != null && itemManager.isFragmentItem(item)) {
                    amount += item.getAmount();
                }
            }

            if (amount < cost) {
                player.sendMessage("§cYou don't have enough frags to upgrade this item.");
                return;
            }

            // Remove the required frags from the player's inventory
            for (final var item : player.getInventory().getContents()) {
                if (item != null && itemManager.isFragmentItem(item)) {
                    if (item.getAmount() > cost) {
                        item.setAmount(item.getAmount() - cost);
                        break;
                    } else {
                        cost -= item.getAmount();
                        item.setAmount(0);
                    }
                }
            }
        }

        // Upgrade the item
        player.getInventory().setItemInMainHand(nextUpgrade.item());
        player.sendMessage("§aItem upgraded.");
    }

}
