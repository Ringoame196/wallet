package com.github.ringoame196.wallet;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Wallet extends JavaPlugin {
    private static JavaPlugin plugin;
    private Events events;
    @Override
    public void onEnable() {
        // プラグインが起動したときの処理
        super.onEnable();
        this.events = new Events();
        Bukkit.getPluginManager().registerEvents(this.events,this);
    }

    @Override
    public void onDisable() {
        // プラグインがシャットダウンするときの処理
        super.onDisable();
    }
    public static JavaPlugin getPlugin(){return plugin;}
}
