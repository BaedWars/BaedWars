package app.myhtl.baedwars.loaders;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.BuyableItem;
import app.myhtl.baedwars.game.ShopCategory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static app.myhtl.baedwars.Server.permanentItems;
import static java.lang.System.exit;

public class ConfigLoader {
    public static Properties loadConfigData() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Path.of("server.properties"))) {
            props.load(in);
        } catch (IOException e) {
            Server.logger.warn("Generated new {}, please configure before starting the server!", "server.properties");
            setupConfigFile("server.properties");
            exit(0);
            return null;
        }
        return props;
    }
    public static Map<UUID, Integer> loadPermissionData() {
        Path path = Path.of("permissions.yml");
        if (!Files.exists(path)) {
            setupConfigFile("permissions.yml");
        }
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();

        CommentedConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException e) {
            Server.logger.error("Could not load permissions.yml", e);
            return Collections.emptyMap();
        }

        Map<UUID, Integer> permissions = new HashMap<>();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : root.childrenMap().entrySet()) {
            String rawKey = String.valueOf(entry.getKey());
            try {
                UUID uuid = UUID.fromString(rawKey);
                permissions.put(uuid, entry.getValue().getInt());
            } catch (IllegalArgumentException ex) {
                Server.logger.warn("Invalid UUID in permissions.yml: {}", rawKey);
            }
        }

        return permissions;
    }
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
    public static void setupConfigFile(String filename) {
        try (InputStream s = Server.class.getResourceAsStream("/" +filename)) {
            assert s != null;
            Files.copy(s, Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
