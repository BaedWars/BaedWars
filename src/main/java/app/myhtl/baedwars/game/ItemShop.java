package app.myhtl.baedwars.game;

import app.myhtl.baedwars.Server;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

import java.util.List;

import static app.myhtl.baedwars.game.ShopCategory.getCategoryFromTitle;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ItemShop {
    public static void openShop(Player player) {
        ShopCategory currentCategory = Server.itemShopData.getFirst();
        player.openInventory(generateInventory(currentCategory, 0));
    }

    public static void handle(InventoryPreClickEvent event) {
        AbstractInventory inventory = event.getInventory();
        if (!inventory.hasTag(Tag.String("TITLE"))) {
            return;
        }

        switch (event.getSlot()) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> changeCategory(event.getSlot(), event.getPlayer(), inventory);
            case 19,20,21,22,23,24,25 -> buyItem(event.getSlot()-19, event.getPlayer(), inventory.getTag(Tag.String("TITLE")));
            case 28,29,30,31,32,33,34 -> buyItem(event.getSlot()-21, event.getPlayer(), inventory.getTag(Tag.String("TITLE")));
        }
        event.setCancelled(true);
    }
    private static void buyItem(int itemIndex, Player player, String categoryTitle) {
        ShopCategory category = getCategoryFromTitle(categoryTitle);
        BuyableItem currentItem = category.buyableItems.get(itemIndex);
        PlayerInventory inventory = player.getInventory();

        if (CoreGame.getFreePlayerInvSlots(player) > 0) {
            if (CoreGame.delFromPlayerInv(player, ItemStack.of(currentItem.priceItem, currentItem.price))) {
                if (currentItem.armorMaterial != null) {
                    var helmetMaterial = Material.fromKey(currentItem.armorMaterial.toLowerCase() + "_helmet");
                    var chestplateMaterial = Material.fromKey(currentItem.armorMaterial.toLowerCase() + "_chestplate");
                    var leggingsMaterial = Material.fromKey(currentItem.armorMaterial.toLowerCase() + "_leggings");
                    var bootsMaterial = Material.fromKey(currentItem.armorMaterial.toLowerCase() + "_boots");
                    player.setHelmet(ItemStack.of(helmetMaterial));
                    player.setChestplate(ItemStack.of(chestplateMaterial));
                    player.setLeggings(ItemStack.of(leggingsMaterial));
                    player.setBoots(ItemStack.of(bootsMaterial));
                } else if (currentItem.placeholderItem != null) {
                    var itemMaterial = Material.fromKey(currentItem.placeholderItem.replace("color", Team.getTeamFromPlayer(player).color).toLowerCase());
                    inventory.addItemStack(ItemStack.of(itemMaterial, currentItem.quantity));
                } else {
                    inventory.addItemStack(ItemStack.of(currentItem.item, currentItem.quantity));
                }
            } else {
                player.sendMessage(Component.text("You don't have enough " + currentItem.priceItem.toString().replace("minecraft:", "") + "s!").color(RED));
            }
        } else {
            player.sendMessage(Component.text("No empty slots left!").color(RED));
        }
    }
    private static void changeCategory(int slot, Player player, AbstractInventory currentInventory) {
        if (currentInventory.getItemStack(slot).material() != Material.AIR) {
            ShopCategory currentCategory = Server.itemShopData.get(slot);
            player.openInventory(generateInventory(currentCategory, slot));
        }
    }
    private static Inventory generateInventory(ShopCategory currentCategory, int index) {
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.text(currentCategory.displayName).color(TextColor.fromHexString("#55555")));
        inventory.setTag(Tag.String("TITLE"), currentCategory.displayName);
        for (int i = 0; i < 9; i++) {
            if (i == index) {
                inventory.setItemStack(i+9, ItemStack.of(Material.LIME_STAINED_GLASS_PANE));
            } else {
                inventory.setItemStack(i+9, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
        for (int i = 0; i < Server.itemShopData.size(); i++) {
            ShopCategory category = Server.itemShopData.get(i);
            ItemStack item = ItemStack.builder(category.icon)
                    .set(DataComponents.ITEM_NAME, Component.text(category.displayName, GREEN))
                    .build();
            inventory.setItemStack(i, item);
        }
        for (int i = 0; i < currentCategory.buyableItems.size(); i++) {
            BuyableItem buyableItem = currentCategory.buyableItems.get(i);
            int[] usableSlots = new int[]{19,20,21,22,23,24,25,28,29,30,31,32,33,34};
            TextColor currencyColor = GRAY;
            String currencyName = buyableItem.priceItem.toString();
            currencyName = switch (buyableItem.priceItem.toString()) {
                case "minecraft:iron_ingot" -> {
                    currencyColor = WHITE;
                    yield "Iron";
                }
                case "minecraft:gold_ingot" -> {
                    currencyColor = GOLD;
                    yield "Gold";
                }
                case "minecraft:emerald" -> {
                    currencyColor = DARK_GREEN;
                    yield "Emerald";
                }
                default -> currencyName;
            };
            ItemStack item = ItemStack.builder(buyableItem.item)
                    .amount(buyableItem.quantity)
                    .set(DataComponents.ITEM_NAME, Component.text(buyableItem.displayName, YELLOW))
                    .set(DataComponents.LORE, List.of(
                            Component.text("Cost: ").color(GRAY).append(Component.text(buyableItem.price + " " + currencyName).color(currencyColor)),
                            Component.text(""),
                            Component.text(buyableItem.description).color(GRAY),
                            Component.text(""),
                            Component.text("Click to purchase!").color(YELLOW)))
                    .build();
            inventory.setItemStack(usableSlots[i], item);
        }
        return inventory;
    }
}
