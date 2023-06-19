package com.github.ringoame196.wallet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private File dataFile;
    private FileConfiguration dataConfig;

    public PlayerData() {
        dataFile = new File("plugins/wallet/data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void setPlayerItems(UUID uuid, List<ItemStack> items) {
        ConfigurationSection playerSection = dataConfig.createSection(uuid.toString());
        List<String> serializedItems = new ArrayList<>();
        for (ItemStack item : items) {
            serializedItems.add(ItemSerializer.serializeItem(item));
        }
        playerSection.set("items", serializedItems);
        saveData();
    }

    public List<ItemStack> getPlayerItems(UUID uuid) {
        ConfigurationSection playerSection = dataConfig.getConfigurationSection(uuid.toString());
        if (playerSection != null) {
            List<ItemStack> items = new ArrayList<>();
            List<String> serializedItems = playerSection.getStringList("items");
            for (String serialized : serializedItems) {
                ItemStack item = ItemSerializer.deserializeItem(serialized);
                if (item != null) {
                    items.add(item);
                }
            }
            return items;
        }
        return new ArrayList<>();
    }

    public void removePlayerItems(UUID uuid) {
        dataConfig.set(uuid.toString(), null);
        saveData();
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}