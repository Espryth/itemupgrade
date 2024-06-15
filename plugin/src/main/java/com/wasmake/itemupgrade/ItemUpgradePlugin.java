package com.wasmake.itemupgrade;

import com.wasmake.itemupgrade.cmd.ItemUpgradeCmd;
import com.wasmake.itemupgrade.command.CommandManager;
import com.wasmake.itemupgrade.items.ItemManager;
import com.wasmake.itemupgrade.serializer.ComponentSerializer;
import com.wasmake.itemupgrade.serializer.ItemStackSerializer;
import com.wasmake.itemupgrade.items.ItemsConfig;
import com.wasmake.itemupgrade.listener.ItemListener;
import com.wasmake.itemupgrade.util.ThrowingConsumer;
import java.io.File;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class ItemUpgradePlugin extends JavaPlugin {

    private CommandManager commandManager;
    private YamlConfigurationLoader configurationLoader;

    private CommentedConfigurationNode config;
    private ItemsConfig itemsConfig;

    @Override
    public void onEnable() {

        final var file = new File(getDataFolder(), "items.yml");

        try {
            this.configurationLoader = YamlConfigurationLoader.builder()
                .file(file)
                .defaultOptions(
                    ConfigurationOptions
                        .defaults()
                        .serializers(
                            TypeSerializerCollection
                                .builder()
                                .registerAll(TypeSerializerCollection.defaults())
                                .register(Component.class, new ComponentSerializer())
                                .register(ItemStack.class, new ItemStackSerializer())
                                .build()
                        )
                )
                .build();

            this.config = this.configurationLoader.load();
            this.itemsConfig = this.config.get(ItemsConfig.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        final var itemManager = new ItemManager(this);

        this.commandManager = new CommandManager(this);
        this.commandManager.addClassCommand(new ItemUpgradeCmd(itemManager));
        this.commandManager.registerCommands();

        // Register event listener
        getServer().getPluginManager().registerEvents(new ItemListener(itemManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ItemsConfig config() {
        return itemsConfig;
    }

    public void updateConfig(final @NotNull ThrowingConsumer<CommentedConfigurationNode> consumer) {
        try {
            consumer.accept(this.config);
            this.configurationLoader.save(this.config);
            this.config = this.configurationLoader.load();
            this.itemsConfig = this.config.get(ItemsConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
