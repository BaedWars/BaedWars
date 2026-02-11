package app.myhtl.baedwars.game;

import net.minestom.server.item.Material;

public class BuyableItem {
    public final Material item; // single item material
    public final String placeholderItem;
    public final int quantity; // quantity of item
    public final String displayName;
    public final String description;
    public final int price;
    public final Material priceItem;
    public final boolean permanent;

    public final String armorMaterial;

    public BuyableItem(String armorMaterial, String itemID, int quantity, String displayName, String description, int price, String PriceItemID, boolean permanent) {
        if (armorMaterial == null && itemID != null) {
            this.armorMaterial = null;
            if (!itemID.contains("COLOR")) {
                this.placeholderItem = null;
            } else {
                this.placeholderItem = itemID.toLowerCase();
            }
        } else {
            this.armorMaterial = armorMaterial;
            this.placeholderItem = null;
        }

        this.item = Material.fromKey(itemID.toLowerCase().replace("color", "white"));
        this.displayName = displayName;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.priceItem = Material.fromKey(PriceItemID.toLowerCase());

        this.permanent = permanent;
    }
}
