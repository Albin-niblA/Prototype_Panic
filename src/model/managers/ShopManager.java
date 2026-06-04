package model.managers;

import model.entities.Player;
import model.items.Item;

import java.util.List;

public class ShopManager {
    private final Player player;
    private final List<Item> shopItems;
    private List<Item> playerInventory;
    private final CoinManager coinManager;


    public ShopManager(Player player, CoinManager coinManager){
        this.player = player;
        this.coinManager = coinManager;
        this.shopItems = initItems();
    }
    private List<Item> initItems() {
        // Constructor format:
        // String name, int textureID, int price
        // int movementspeed, int health, int damage
        // double attackspeed, int onHit, int blinkDistance
        return List.of(
                new Item("Life Potion",  8,   50,  0,   0, 0.20, 0, 0, 0, 0),
                new Item("Boots", 0, 100, 50, 0, 0, 0, 0, 0),
                new Item("Shadow boots", 1, 500, 80, 0, 0, 0, 0, 100),
                new Item("Shield", 2, 1000, 0, 100, 0, 0, 0, 0)
        );
    }

    public boolean buyItem(int index){
        if (index < 0 || index >= shopItems.size()) return false;
        Item item = shopItems.get(index);
        if (!coinManager.canAfford(item.getPrice())) return false;
        coinManager.spend(item.getPrice());
        applyItem(item);
        return true;
    }

    private void applyItem(Item item){
        if(item.getHealthPercent() > 0){
            int heal = (int)(player.getMaxHealth() * item.getHealthPercent());
            player.addHealth(heal);
        }
        if(item.getHealth() > 0){
            player.setMaxHealth(player.getMaxHealth() + item.getHealth());
        }
        if(item.getOnHit() > 0){
            player.getUpgradeManager().addOnHitDamage(item.getOnHit());
        }
    }
    public List<Item> getShopItems(){
        return shopItems;
    }

}
