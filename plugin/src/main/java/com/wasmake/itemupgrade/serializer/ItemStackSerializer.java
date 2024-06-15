package com.wasmake.itemupgrade.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(final Type type, final ConfigurationNode node) throws SerializationException {

        final var str = node.getString();

        if (str == null) {
            return null;
        }

        try {
            final var inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(str));
            final var dataInput = new BukkitObjectInputStream(inputStream);
            final var itemStack = (ItemStack) dataInput.readObject();
            dataInput.close();
            return itemStack;
        } catch (final ClassNotFoundException | IOException e) {
            throw new SerializationException(e);
        }

        /*

        final var material = Material.matchMaterial(node.node("type")
            .getString(Material.BEDROCK.name()));

        if (material == null) {
            return new ItemStack(Material.BEDROCK);
        }

        var item = new ItemStack(material, node.node("amount").getInt(1));

        final var customEnchants = node.node("custom-enchants").getList(String.class);

        if (customEnchants != null) {
            final var nmsItem = CraftItemStack.asNMSCopy(item);
            final var compoundTag = nmsItem.getOrCreateTag();
            for (final var enchantStr : customEnchants) {
                final var split = enchantStr.split(":");
                System.out.println("ENCHANT: " + split[0] + " " + split[1]);
                compoundTag.put(split[0], IntTag.valueOf(Integer.parseInt(split[1])));
            }
            nmsItem.setTag(compoundTag);
            item = CraftItemStack.asBukkitCopy(nmsItem);
        }

        final var itemMeta = item.getItemMeta();
        final var displayName = node.node("display-name").get(Component.class);

        if (displayName != null) {
            itemMeta.displayName(displayName);
        }

        final var lore = node.node("lore").getList(Component.class);

        if (lore != null) {
            itemMeta.lore(lore);
        }

        final var enchants = node.node("enchantments").getList(String.class);

        if (enchants != null) {
            for (final var enchantStr : enchants) {
                final var split = enchantStr.split(":");
                @SuppressWarnings("deprecation")
                final var enchantment = Enchantment.getByName(split[0]);
                if (enchantment != null) {
                    try {
                        itemMeta.addEnchant(enchantment, Integer.parseInt(split[1]), true);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        final var flags = node.node("flags").getList(String.class);

        if (flags != null) {
            for (final var flag : flags) {
                try {
                    itemMeta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        item.setItemMeta(itemMeta);
        return item;

         */
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {

        if (obj == null) {
            node.raw(null);
            return;
        }

        try {
            final var outputStream = new ByteArrayOutputStream();
            final var dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(obj);
            dataOutput.close();
            node.set(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new SerializationException(e);
        }

/*
        if (obj == null) {
            node.raw(null);
            return;
        }

        final var itemMeta = obj.getItemMeta();

        node.node("type").set(obj.getType().name());
        node.node("amount").set(obj.getAmount());

        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                node.node("display-name").set(itemMeta.displayName());
            }

            if (itemMeta.hasLore()) {
                node.node("lore").setList(Component.class, itemMeta.lore());
            }

            @SuppressWarnings("deprecation")
            final var enchants = itemMeta.getEnchants().entrySet().stream()
                .map(entry -> entry.getKey().getName() + ":" + entry.getValue())
                .toList();

            if (!enchants.isEmpty()) {
                node.node("enchantments").setList(String.class, enchants);
            }

            final var flags = itemMeta.getItemFlags().stream().map(ItemFlag::name).toList();

            if (!flags.isEmpty()) {
                node.node("flags").setList(String.class, flags);
            }
        }

        final var nmsItem = CraftItemStack.asNMSCopy(obj);
        final var compoundTag = nmsItem.getTag();
        if (compoundTag != null) {
            final var tags = new ArrayList<String>();
            for (final var key : compoundTag.getAllKeys()) {
                final var tag = compoundTag.get(key);
                System.out.println("key: " + key + " tag: " + tag);
                if (key.startsWith("ae_enchantment")) {
                    System.out.println("ADDED " + tag.getType().getName());
                    tags.add(key + ":" + tag.getAsString());
                }
            }
            node.node("custom-enchants").set(tags);
        }


 */
    }
}
