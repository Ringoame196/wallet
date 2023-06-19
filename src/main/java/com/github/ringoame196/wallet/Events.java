package com.github.ringoame196.wallet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Events implements Listener {
    private final PlayerData playerData;

    public Events() {
        playerData = new PlayerData();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        //アイテムクリック時の処理
        Player player = e.getPlayer();//プレイヤー取得
        ItemStack item = e.getItem();//クリックアイテム取得
        if(item==null) {return;}//アイテムを取得できなかったときは処理をしない
        if (!item.getType().equals(Material.SLIME_BALL) || !Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals("財布")) {return;}//財布以外をクリックしてたら処理を無効
        e.setCancelled(true);//イベントキャンセル
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN,1,1);//チェストのOPENの音

        //GUIを開く
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + "財布");
        player.openInventory(inv);

        //下2行でデータを取得する(らしい)
        UUID playerUUID = player.getUniqueId();
        List<ItemStack> playerItems = playerData.getPlayerItems(playerUUID);

        //既にデータが保存されているなら そのデータを取ってアイテム設置
        if (playerItems != null) {
            for (ItemStack player_item : playerItems) {
                inv.addItem(player_item);
            }
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        //インベントリを閉じる処理
        if (!(e.getPlayer() instanceof Player)) {return;}//プレイヤー以外の場合処理をさせない
        Player player = (Player) e.getPlayer();//プレイヤー
        String inv_name = e.getView().getTitle();//GUIの名前取得
        Inventory inv = e.getInventory();//GUI取得
        if (!inv_name.equals(ChatColor.DARK_AQUA + "財布")) {return;}//財布以外だったら処理をしない

        int n = 0;//合計ベリル数管理

        int errormessage = 0;//エラーメッセージ
        List<ItemStack> wallet_items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack check_item = inv.getItem(i);//GUI(財布)のスロットアイテムを一個一個チェック
            if (check_item != null) {
                if (!check_item.getType().equals(Material.SCUTE)||!Objects.requireNonNull(Objects.requireNonNull(check_item.getItemMeta()).getLore()).contains(ChatColor.RED+"通貨")) {
                    //ベリル以外だったら アイテム返却+メッセージ送信
                    player.getInventory().addItem(check_item);
                    if(errormessage==0) {
                        //複数メッセージ対策
                        player.sendMessage(ChatColor.RED + "お金以外を入れることはできません");
                        errormessage=1;
                    }
                }
                else{
                    //ベリルの処理
                    wallet_items.add(check_item);//アイテムを保存
                    String name = check_item.getItemMeta().getDisplayName();//アイテム名を取得
                    int c = 0;
                    if(name.contains("1ベリル"))//アイテム名で お金の価値をチェック
                    {
                        c=1;
                    }
                    else{
                        c=64;
                    }
                    n+=c*check_item.getAmount();//nに値段*個数を足す
                }
            }
        }
        //財布のloreに合計を表示する処理
        ItemStack main_inv = player.getInventory().getItemInMainHand();
        ItemMeta meta = main_inv.getItemMeta();
        assert meta != null;
        meta.setLore(Collections.singletonList(ChatColor.GREEN + "合計:" + n + "ベリル"));
        main_inv.setItemMeta(meta);
        player.getInventory().setItemInMainHand(main_inv);

        //合計額をお知らせ
        player.sendMessage(ChatColor.AQUA +"[財布] 合計:"+ n +"ベリル");
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE,1,1);

        //多分データを保存？
        UUID playerUUID = player.getUniqueId();
        playerData.setPlayerItems(playerUUID, wallet_items);
    }
}
