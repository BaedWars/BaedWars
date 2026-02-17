package app.myhtl.baedwars.loaders;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.BuyableItem;
import app.myhtl.baedwars.game.ShopCategory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static app.myhtl.baedwars.Server.permanentItems;
import static app.myhtl.baedwars.loaders.ConfigLoader.setupConfigFile;

public class ShopLoader {

    public static List<ShopCategory> loadItemShopData() {
        Path path = Path.of("itemShop.yml");
        if (!Files.exists(path)) {
            setupConfigFile("itemShop.yml");
        }
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path) // Set where we will load and save to
                .build();
        CommentedConfigurationNode root;

        try {
            root = loader.load().node("categories");
        } catch (IOException e) {
            setupConfigFile("itemShop.yml");
            return null;
        }

        List<ShopCategory> shopCategories = new ArrayList<>();

        if (!root.empty()) {
            for (CommentedConfigurationNode rawCategory : root.childrenList()) {
                var catIndex = rawCategory.node("index").getInt();
                var catIconID = rawCategory.node("icon").getString();
                var catDisplayName = rawCategory.node("display_name").getString();
                var items = rawCategory.node("items");

                if (!items.childrenList().isEmpty()) {
                    List<BuyableItem> buyableItems = new ArrayList<>();
                    for (CommentedConfigurationNode rawItem : items.childrenList()) {
                        String itemID = rawItem.node("id").getString();
                        String itemDisplayName = rawItem.node("display_name").getString();
                        int itemQuantity = rawItem.node("quantity").getInt();
                        String itemDescription = rawItem.node("description").getString();
                        int itemPrice = rawItem.node("price").getInt();
                        String itemPriceItemID = rawItem.node("price_item").getString();

                        boolean permanent = rawItem.node("permanent").getBoolean();

                        boolean isArmorSet = rawItem.hasChild("armor_material");
                        String armorMaterial = isArmorSet ? rawItem.node("armor_material").toString() : null;

                        BuyableItem item = new BuyableItem(armorMaterial, itemID, itemQuantity, itemDisplayName, itemDescription, itemPrice, itemPriceItemID, permanent);
                        buyableItems.add(item);

                        if (permanent) {
                            permanentItems.add(item);
                        }
                    }
                    assert catIconID != null;
                    shopCategories.add(new ShopCategory(catIndex, catIconID, catDisplayName, buyableItems));
                }
            }
        }
        return shopCategories;
    }
}
