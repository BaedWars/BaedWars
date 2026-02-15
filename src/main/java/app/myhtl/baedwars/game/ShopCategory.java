package app.myhtl.baedwars.game;

import app.myhtl.baedwars.Server;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.Objects;

public class ShopCategory {
    public int index;
    public Material icon;
    public String displayName;
    public List<BuyableItem> buyableItems;
    public ShopCategory(int index, String iconID, String displayName, List<BuyableItem> buyableItems) {
        this.index = index;
        this.icon = Material.fromKey(iconID.toLowerCase());
        this.displayName = displayName;
        this.buyableItems = buyableItems;
    }
    public static ShopCategory getCategoryFromTitle(String categoryTitle) {
        for (ShopCategory category : Server.itemShopData) {
            if (Objects.equals(category.displayName, categoryTitle)) {
                return category;
            }
        }
        return Server.itemShopData.getFirst();
    }
}
