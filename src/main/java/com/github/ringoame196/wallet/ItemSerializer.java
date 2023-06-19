package com.github.ringoame196.wallet;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Base64;

public class ItemSerializer {
    public static String serializeItem(ItemStack item) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", item);
        String serialized = config.saveToString();
        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    public static ItemStack deserializeItem(String serialized) {
        byte[] data = Base64.getDecoder().decode(serialized);
        String decoded = new String(data);
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(decoded);
            return config.getItemStack("item");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}