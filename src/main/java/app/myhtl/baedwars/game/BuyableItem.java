package app.myhtl.baedwars.game;

import net.minestom.server.item.Material;

public class BuyableItem {
    public final Material item;
    public final int quantity;
    public final String displayName;
    public final String description;
    public final int price;
    public final Material priceItem;
    public BuyableItem(String itemID, int quantity, String displayName, String description, int price, String PriceItemID) {
        this.displayName = displayName;
        this.item = Material.fromKey(itemID.toLowerCase());
        this.quantity = quantity;
        this.description = description;
        this.price = price;
        this.priceItem = Material.fromKey(PriceItemID.toLowerCase());
    }
}
