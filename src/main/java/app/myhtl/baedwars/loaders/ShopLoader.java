package app.myhtl.baedwars.loaders;

import app.myhtl.baedwars.game.BuyableItem;
import app.myhtl.baedwars.game.ShopCategory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static app.myhtl.baedwars.Server.permanentItems;

public class ShopLoader {
    public static ShopCategory[] loadItemShopData() {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("itemShop.yml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Yaml yaml = new Yaml();
        Map<String, Object> rawData = yaml.load(inputStream);
        ShopCategory[] shopCategories = new ShopCategory[0];
        if (rawData.values().toArray()[0] instanceof ArrayList<?> rawCategories) {
            shopCategories = new ShopCategory[rawCategories.size()];
            for (int i=0; i<rawCategories.size(); i++) {
                Object rawCategory = rawCategories.get(i);
                if (rawCategory instanceof LinkedHashMap<?, ?> mappedCategory) {
                    int catIndex = (int) mappedCategory.get("index");
                    String catIconID = (String) mappedCategory.get("icon");
                    String catDisplayName = (String) mappedCategory.get("display_name");
                    BuyableItem[] buyableItems = new BuyableItem[0];
                    if (mappedCategory.get("items") instanceof ArrayList<?> rawItems) {
                        buyableItems = new BuyableItem[rawItems.size()];
                        for (int j=0; j<rawItems.size();j++) {
                            Object rawItem = rawItems.get(j);
                            if (rawItem instanceof LinkedHashMap<?, ?> mappedItem) {
                                String itemID = (String) mappedItem.get("id");
                                String itemDisplayName = (String) mappedItem.get("display_name");
                                int itemQuantity = (int) mappedItem.get("quantity");
                                String itemDescription = (String) mappedItem.get("description");
                                int itemPrice = (int) mappedItem.get("price");
                                String itemPriceItemID = (String) mappedItem.get("price_item");

                                boolean permanent = false;
                                if (mappedItem.get("permanent") instanceof Boolean b) permanent = b;

                                boolean isArmorSet = mappedItem.containsKey("armor_material");
                                String armorMaterial = isArmorSet ? (String) mappedItem.get("armor_material") : null;

                                buyableItems[j] = new BuyableItem(armorMaterial, itemID, itemQuantity, itemDisplayName, itemDescription, itemPrice, itemPriceItemID, permanent);

                                if (buyableItems[j].permanent) {
                                    permanentItems.add(buyableItems[j]);
                                }
                            }
                        }
                    }
                    shopCategories[i] = new ShopCategory(catIndex, catIconID, catDisplayName, buyableItems);
                }
            }
        }
        return shopCategories;
    }
}
