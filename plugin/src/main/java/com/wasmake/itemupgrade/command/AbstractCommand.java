package com.wasmake.itemupgrade.command;

import com.wasmake.itemupgrade.ItemUpgradePlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractCommand {
    private String name;
    private String[] aliases;

    public AbstractCommand(String name, String... aliases){
        this.name = name;
        this.aliases = aliases;
        JavaPlugin.getPlugin(ItemUpgradePlugin.class)
            .getCommandManager()
            .getCommands()
            .add(this);
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

}