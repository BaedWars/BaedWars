package app.myhtl.baedwars.game;

import app.myhtl.baedwars.handlers.ShowGameSidebar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import app.myhtl.baedwars.Server;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import org.yaml.snakeyaml.Yaml;

import static app.myhtl.baedwars.Server.permanentItems;
import static app.myhtl.baedwars.Server.round_id;
import static app.myhtl.baedwars.game.Team.getTeamFromPlayer;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CoreGame {
    public static String gameType = "1v1";
    public static Team[] teams;
    public static Sidebar lobbySidebar;
    public static int playersPerTeams = generateGameTypeData()[0];
    public static int teamsAmount = generateGameTypeData()[1];
    public static int totalPlayers = 0;
    public static final Component serverPrefix = Component.text("[").color(GRAY).append(Component.text("BaedWars").color(NamedTextColor.AQUA), Component.text("] ").color(GRAY));
    public static void startGame(Scheduler scheduler) {
        int countdownDuration = 5;
        AtomicInteger counter = new AtomicInteger();
        if (totalPlayers != playersPerTeams*teamsAmount) {
            return;
        }
        scheduler.submitTask(() -> {
            Audiences.players().sendMessage(Component.text("The game starts in ").color(YELLOW).append(Component.text(countdownDuration - counter.get()).color(RED)).append(Component.text(" seconds!").color(YELLOW)));
            counter.getAndIncrement();
            if (counter.get() <= countdownDuration) {
                return TaskSchedule.seconds(1);
            }
            Server.gameStarted = true;
            Audiences.players().clearTitle();
            for (Team team : teams) {
                for (Player player : team.players) {
                    if (player != null) {
                        player.teleport(team.spawnPos);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setInvisible(false);
                        CoreGame.lobbySidebar.removeViewer(player);
                        getTeamFromPlayer(player).sidebar.addViewer(player);
                    }
                }
            }
            return TaskSchedule.stop();
        });
    }
    public static void stopGame(Scheduler scheduler) {
        for (Team team : teams) {
            Title title;
            Sound sound;
            if (team.alivePlayers == 0) {
                sound = Sound.sound(SoundEvent.MUSIC_DISC_CHIRP, Sound.Source.UI, 1000.0f, 1.0f);
                title = Title.title(Component.text("GAME OVER!").color(RED).decorate(TextDecoration.BOLD), Component.empty());
            } else {
                sound = Sound.sound(SoundEvent.MUSIC_DISC_LAVA_CHICKEN, Sound.Source.UI, 1000.0f, 1.0f);
                title = Title.title(Component.text("VICTORY!").color(YELLOW), Component.text(""));
            }
            for (Player player : team.players) {
                if (player != null) {
                    player.getInventory().clear();
                    player.stopSound(SoundStop.all());
                    player.playSound(sound);
                    player.showTitle(title);
                }
            }
        }
        Audiences.players().sendMessage(Component.text("You can leave with /lobby"));
        final int[] i = {-5};
        scheduler.submitTask(() -> {
            i[0] += 5;
            if (i[0] == 20) {
                for (Team team : teams) {
                    for (Player player : team.players) {
                        if (player != null) {
                            player.sendPluginMessage("bungeecord:main", NetworkBuffer.makeArray(buffer -> {
                                buffer.write(NetworkBuffer.STRING_IO_UTF8, "Connect");
                                buffer.write(NetworkBuffer.STRING_IO_UTF8, "lobby");
                            }));
                        }
                    }
                }
                MinecraftServer.stopCleanly();
            } else if (i[0] != 0) {
                Audiences.players().sendMessage(Component.text("Restart in ").append(Component.text(20 - i[0]).color(AQUA)).append(Component.text(" Seconds")));
            }
            return TaskSchedule.seconds(5);
        });
    }
    public static void createTeams() {
        teams = new Team[teamsAmount];
        for (int i = 0; i < teamsAmount; i++) {
            teams[i] = new Team();
            teams[i].players = new Player[playersPerTeams];
            teams[i].playerUUIDs = new UUID[playersPerTeams];
            teams[i].color = Server.map.teamColors[i];
            teams[i].spawnPos = Server.map.teamSpawnPoints[i];
            teams[i].alivePlayers = playersPerTeams;
        }
        for (int i = 0; i < teamsAmount; i++) {
            teams[i].sidebar = CoreGame.generateTeamSidebar(teams[i]);
        }
    }
    public static void joinRandomTeam(Player player) {
        int randomTeamIndex = (int) (Math.random() * ((teams.length)));
        if (teams[randomTeamIndex].players[0] == null) {
            teams[randomTeamIndex].players[0] = player;
            teams[randomTeamIndex].playerUUIDs[0] = player.getUuid();
            teams[randomTeamIndex].enderChests.put(player.getUuid(), new Inventory(InventoryType.CHEST_3_ROW, Component.text("Ender Chest")));
            totalPlayers++;
            Server.logger.info("Player {} was assigned to the {} Team", player.getUsername(), teams[randomTeamIndex].color);
        } else {
            joinRandomTeam(player);
        }
    }
    public static Sidebar generateTeamSidebar(Team ownTeam) {
        Sidebar sidebar = new Sidebar(Component.text("    BED WARS    ").color(YELLOW).decorate(TextDecoration.BOLD));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "date&id",
                Component.text(new SimpleDateFormat("dd.MM.yy").format(new Date()), GRAY).append(Component.text("    " + round_id, DARK_GRAY)),
                12
        ));
        sidebar.updateLineNumberFormat("date&id", Sidebar.NumberFormat.blank());

        sidebar.createLine(new Sidebar.ScoreboardLine(
                "empty01",
                Component.empty(),
                11
        ));
        sidebar.updateLineNumberFormat("empty01", Sidebar.NumberFormat.blank());
        for (int i=0; i<CoreGame.teams.length; i++) {
            Team team = CoreGame.teams[i];
            String colorString = team.color.substring(0, 1).toUpperCase() + team.color.substring(1).toLowerCase();
            if (team == ownTeam) {
                sidebar.createLine(new Sidebar.ScoreboardLine(
                        "team0" + (i - 1),
                        Component.text(team.color.toUpperCase().charAt(0)).color(NamedTextColor.NAMES.value(team.color.toLowerCase())).append(Component.text(" " + colorString + ":").color(WHITE)).append(Component.text(" ✔ ").color(GREEN)).append(Component.text("YOU").color(GRAY)),
                        i + 2
                ));
            } else {
                sidebar.createLine(new Sidebar.ScoreboardLine(
                        "team0" + (i - 1),
                        Component.text(team.color.toUpperCase().charAt(0)).color(NamedTextColor.NAMES.value(team.color.toLowerCase())).append(Component.text(" " + colorString + ":").color(WHITE)).append(Component.text(" ✔ ").color(GREEN)),
                        i + 2
                ));
            }
            sidebar.updateLineNumberFormat("team0" + (i - 1), Sidebar.NumberFormat.blank());
        }

        sidebar.createLine(new Sidebar.ScoreboardLine(
                "empty02",
                Component.empty(),
                0
        ));
        sidebar.updateLineNumberFormat("empty02", Sidebar.NumberFormat.blank());
        return sidebar;
    }
    public static void updateTeamSidebar(Team ownTeam, Scheduler scheduler) {
        Sidebar sidebar = ownTeam.sidebar;
        for (int i=0; i<CoreGame.teams.length; i++) {
            Team team = CoreGame.teams[i];
            for (Player player : team.players) {
                if (player != null) {
                    if (player.getGameMode() != GameMode.SURVIVAL && team.alivePlayers > 0) {
                        team.alivePlayers--;
                    }
                }
            }
            if (team.alivePlayers == 0) {
                stopGame(scheduler);
            }
            String colorString = team.color.substring(0, 1).toUpperCase() + team.color.substring(1).toLowerCase();
            Component mainComponent = Component.text(team.color.toUpperCase().charAt(0)).color(NamedTextColor.NAMES.value(team.color.toLowerCase())).append(Component.text(" " + colorString + ":").color(WHITE));
            if (team.bedDestroyed) {
                if (team.alivePlayers == 0) {
                    mainComponent = mainComponent.append(Component.text(" ✘ ").color(RED));
                } else {
                    mainComponent = mainComponent.append(Component.text(" " + team.alivePlayers + " ").color(GREEN));
                }
            } else {
                mainComponent = mainComponent.append(Component.text(" ✔ ").color(GREEN));
            }
            if (team == ownTeam) {
                mainComponent = mainComponent.append(Component.text("YOU").color(GRAY));
            }
            sidebar.updateLineContent("team0" + (i - 1), mainComponent);
        }
    }
    public static void generateLobbySidebar() {
        lobbySidebar = new Sidebar(Component.text("    BED WARS    ").color(YELLOW).decorate(TextDecoration.BOLD));
        lobbySidebar.createLine(new Sidebar.ScoreboardLine(
                "date&id",
                Component.text(new SimpleDateFormat("dd.MM.yy").format(new Date()), GRAY).append(Component.text("    " + round_id, DARK_GRAY)),
                12
        ));
        lobbySidebar.updateLineNumberFormat("date&id", Sidebar.NumberFormat.blank());

        lobbySidebar.createLine(new Sidebar.ScoreboardLine(
                "empty01",
                Component.empty(),
                11
        ));
        lobbySidebar.updateLineNumberFormat("empty01", Sidebar.NumberFormat.blank());

        lobbySidebar.createLine(new Sidebar.ScoreboardLine(
                "playerCount",
                Component.text("Players: " + totalPlayers + "/" + playersPerTeams*teamsAmount),
                10
        ));
        lobbySidebar.updateLineNumberFormat("playerCount", Sidebar.NumberFormat.blank());

        lobbySidebar.createLine(new Sidebar.ScoreboardLine(
                "empty02",
                Component.empty(),
                0
        ));
        lobbySidebar.updateLineNumberFormat("empty02", Sidebar.NumberFormat.blank());
    }
    public static void updateLobbySidebar() {
        int totalPlayers = 0;
        for (Team team : teams) {
            for (Player player : team.players) {
                if (player != null) {
                    totalPlayers++;
                }
            }
        }
        lobbySidebar.updateLineContent("playerCount",
                Component.text("Players: " + totalPlayers + "/" + playersPerTeams*teamsAmount)
        );
    }
    public static NPC[] summonNPCs(InstanceContainer instance) {
        String skinTexture = "ewogICJ0aW1lc3RhbXAiIDogMTYxODg3Mzc5NzAxMCwKICAicHJvZmlsZUlkIiA6ICI2MWVhMDkyM2FhNDQ0OTEwYmNlZjViZmQ2ZDNjMGQ1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVEYXJ0aEZhdGhlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZGUzZjg4ZjFhZjRhMzEwZTIxNDgyYzU5NmY3ZTQzOTU0MmZlN2JhMjJmZjE1M2M3NWZjNGM2Yjg4NDVjYjM3IgogICAgfQogIH0KfQ==";
        String skinSignature = "h3FaOC0oipvtoiRFPyLj+j5r7OXQaUwSWnK9m148/n2O+Uvek7KOt4I1aVXAdDVGpJ1TvXHzBYjNYaXs69Y0WGLY2KwxtHIFh0YNeFLREH37dngRrqyTyp/hi2ZiolRLeGQgaLOMl5qMKFKbPKExAUntZCzNntYjtdsdA9ZyYreE8pZN4GEpuXBB/HiT7CGfUXlLlxGqd0NAISO6sQ7Sz9DHFvZi2nQBLjMjAhtdDeyE+XhUgmesyBTPktccxDkB4bPljuPb/1ZgQSxGVKFRie/Y7xGYk+LFEQ2p7HyIXJaZvUVda80WVyzExNZ3SgN/KCqml96MxtXzYeAaCEUAVUZt0YlWQljhPgVqtOIpPpMNMliYS7WncLrGzI5dDuDPn++IYxiySQepSczbHZTjeV4mvyXiyeMK0d4IQW51W4tE/B8OZAE52rhFICN7P9mCtUBnvPF82HMkyyWgLJcaeguy+LVcOSoMJp0/lZMMzQCsTJPzLBxCSp4ujrulC0C9/vsbezx8LGbTMqul6erqMl6MeAGLvNlyzZhMGiD5SmBwOEHDNjPV4hK0pll9T4G8qgTGaU3SmcEU6/x35o5VMnluTv6+yr7xTQekK5jz5Ae62fClh4lOJr3CJLVLJqPU60+Gpe0FcMun1sn8MPwmTwAQAdDjpQ2Dkx5f4/B9i4I=";
        List<NPC> npcs = new ArrayList<>();
        Pos[] itemShop = Server.map.itemShopNPCPositions;
        Pos[] teamShop = Server.map.teamShopNPCPositions;
        int n = Server.map.numberOfTeams;
        for (int i = 0; i < n; i++) {
            if (itemShop != null && i < itemShop.length) {
                Pos p = itemShop[i];
                if (p != null) {
                    NPC npc = new NPC("Item-Shop", skinTexture, skinSignature, "SHOP");
                    npc.setInstance(instance, p);
                    npcs.add(npc);
                }
            }
            if (teamShop != null && i < teamShop.length) {
                Pos p = teamShop[i];
                if (p != null) {
                    NPC npc = new NPC("Team-Shop", skinTexture, skinSignature, "TEAMSHOP");
                    npc.setInstance(instance, p);
                    npcs.add(npc);
                }
            }
        }
        return npcs.toArray(new NPC[0]);
    }
    public static int[] generateGameTypeData() {
        String[] gameTypeData = gameType.split("v");
        int playersPerTeams = Integer.parseInt(gameTypeData[0]);
        int teamsAmount = gameTypeData.length;
        return new int[]{playersPerTeams, teamsAmount};
    }

    public static Pos getSpawnPos(Player player) {
        return getTeamFromPlayer(player).spawnPos;
    }

    public static ShopCategory[] loadItemShopData() {
        InputStream inputStream = null;
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

    public static void killPlayer(Player player, Scheduler scheduler) {
        int respawnDuration = 5;
        AtomicInteger counter = new AtomicInteger();
        Team team = getTeamFromPlayer(player);
        player.teleport(new Pos(-13, 85, -18), new Vec(0));
        player.setGameMode(GameMode.SPECTATOR);
        player.closeInventory();
        player.getInventory().clear();
        player.heal();
        if (team.bedDestroyed) {
            updateTeamSidebar(team, scheduler);
            ShowGameSidebar.handle(player);
            player.sendMessage(Component.text("You've been eliminated!").color(RED));
            return;
        }
        player.sendTitlePart(TitlePart.TITLE, Component.text("YOU DIED!").color(RED));
        scheduler.submitTask(() -> {
            player.sendTitlePart(TitlePart.SUBTITLE, Component.text("You will respawn in ").color(YELLOW).append(Component.text(respawnDuration - counter.get()).color(RED)).append(Component.text(" Seconds").color(YELLOW)));
            counter.getAndIncrement();
            if (counter.get() <= respawnDuration) {
                return TaskSchedule.seconds(1);
            }
            player.clearTitle();
            player.removeTag(Tag.Double("lastPos"));
            player.removeTag(Tag.Boolean("isAir"));
            player.teleport(team.spawnPos);
            player.setGameMode(GameMode.SURVIVAL);
            return TaskSchedule.stop();
        });
    }
    public static int getFreePlayerInvSlots(Player player) {
        int emptySlot = 0;
        PlayerInventory inv = player.getInventory();
        for (int i=0; i<inv.getItemStacks().length-1; i++) {
            ItemStack stack = inv.getItemStack(i);
            if (stack.material() == Material.AIR) {
                emptySlot++;
            }
        }
        if (emptySlot <= 9) {
            return 0;
        }
        return emptySlot - 9;
    }
    public static boolean delFromPlayerInv(Player player, ItemStack delStack) {
        PlayerInventory inv = player.getInventory();
        for (int i=0; i<inv.getItemStacks().length-1; i++) {
            ItemStack stack = inv.getItemStack(i);
            if (stack.material() == delStack.material()) {
                if (stack.amount() > delStack.amount()) {
                    inv.setItemStack(i, stack.withAmount(stack.amount() - delStack.amount()));
                } else if (stack.amount() == delStack.amount()) {
                    inv.setItemStack(i, ItemStack.AIR);
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
