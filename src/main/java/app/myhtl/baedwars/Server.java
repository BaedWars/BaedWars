package app.myhtl.baedwars;

import app.myhtl.baedwars.commands.AdminCommands;
import app.myhtl.baedwars.handlers.blocks.Bed;
import app.myhtl.baedwars.handlers.blocks.Beehive;
import app.myhtl.baedwars.handlers.blocks.Chest;
import app.myhtl.baedwars.handlers.blocks.EnderChest;
import app.myhtl.baedwars.helpers.ConsoleInputTask;
import app.myhtl.baedwars.loaders.ConfigLoader;
import app.myhtl.baedwars.loaders.WorldLoader;
import io.github.togar2.pvp.MinestomPvP;
import io.github.togar2.pvp.feature.CombatFeatureSet;
import io.github.togar2.pvp.feature.CombatFeatures;
import io.github.togar2.pvp.feature.FeatureType;
import io.github.togar2.pvp.feature.provider.DifficultyProvider;
import io.github.togar2.pvp.utils.CombatVersion;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;

import app.myhtl.baedwars.listeners.*;
import app.myhtl.baedwars.game.*;
import app.myhtl.baedwars.game.ItemGen;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revxrsal.commands.minestom.MinestomLamp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Server {
    public static long startTime = System.nanoTime();
    public static Logger logger = LoggerFactory.getLogger(Server.class);
    public static Properties config = ConfigLoader.loadConfigData();
    public static List<BuyableItem> permanentItems = new ArrayList<>();
    public static Map<UUID, Integer> permissionData = ConfigLoader.loadPermissionData();
    public static List<ShopCategory> itemShopData = ConfigLoader.loadItemShopData();
    public static boolean gameStarted = false;
    public static World map = WorldLoader.load(Path.of("worlds/" + config.getProperty("map-name") + ".yml"));
    public static NPC[] npcs;
    public static String round_id = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    public static final List<Block> bedList = List.of(
            Block.BLACK_BED, Block.BLUE_BED, Block.BROWN_BED, Block.CYAN_BED,
            Block.GRAY_BED, Block.GREEN_BED, Block.LIGHT_BLUE_BED, Block.LIGHT_GRAY_BED,
            Block.LIME_BED, Block.MAGENTA_BED, Block.ORANGE_BED, Block.PINK_BED,
            Block.PURPLE_BED, Block.RED_BED, Block.WHITE_BED, Block.YELLOW_BED
    );

    public static void main(String[] args) {
        Auth auth;
        try {
            auth = new Auth.Velocity(Files.readString(Path.of("forwarding.secret")));
        } catch (IOException ignored) {
            if (Objects.equals(config.getProperty("online-mode", "true"), "true")) {
                auth = new Auth.Online();
            } else {
                auth = new Auth.Offline();
            }
        }
        MinecraftServer minecraftServer = MinecraftServer.init(auth);

        Thread consoleThread = new Thread(new ConsoleInputTask());
        consoleThread.setDaemon(true);
        consoleThread.start();
        MinestomPvP.init();

        MinecraftServer.getBlockManager().registerHandler("minecraft:chest", Chest::new);
        MinecraftServer.getBlockManager().registerHandler("minecraft:ender_chest", EnderChest::new);
        MinecraftServer.getBlockManager().registerHandler("minecraft:beehive", Beehive::new);
        MinecraftServer.getBlockManager().registerHandler("minecraft:bed", Bed::new);

        CombatFeatureSet modernVanilla = CombatFeatures.getVanilla(CombatVersion.MODERN, DifficultyProvider.DEFAULT)
                .remove(FeatureType.FOOD)
                .remove(FeatureType.EXHAUSTION)
                .build();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        instanceContainer.setChunkLoader(new AnvilLoader(map.savePath));
        instanceContainer.setExplosionSupplier(modernVanilla.get(FeatureType.EXPLOSION).getExplosionSupplier());
        instanceContainer.setTimeRate(0);
        instanceContainer.setTime(6000);
        instanceContainer.setBlock(Server.map.lobbySpawnPos.blockX(), Server.map.lobbySpawnPos.blockY()-1, Server.map.lobbySpawnPos.blockZ(), Block.BARRIER);

        var handler = MinecraftServer.getGlobalEventHandler();
        handler.addChild(modernVanilla.createNode());
        handler.addChild(AllEventListener.getPlayerEvent(instanceContainer, scheduler));
        handler.addChild(PreGameEventListener.getPlayerEvent(scheduler));
        handler.addChild(DeadEventListener.getPlayerEvent(scheduler));
        handler.addChild(VanillaEventListener.getPlayerEvent(scheduler));
        handler.addChild(BedwarsEventListener.getPlayerEvent(scheduler));
        handler.addChild(SpectatorEventListener.getPlayerEvent());

        Pos[] posIrGoSpawner = map.ironSpawnerPos;
        Pos[] posDiaSpawner = map.diaSpawnerPos;
        scheduler.submitTask(() -> {
            ItemGen.item(posIrGoSpawner, instanceContainer, Material.IRON_INGOT);
            return TaskSchedule.millis(1500);
        });
        scheduler.submitTask(() -> {
            ItemGen.item(posIrGoSpawner, instanceContainer, Material.GOLD_INGOT);
            return TaskSchedule.seconds(6);
        });
        scheduler.submitTask(() -> {
            ItemGen.item(posDiaSpawner, instanceContainer, Material.DIAMOND);
            return TaskSchedule.seconds(15);
        });
        scheduler.submitTask(() -> {
            ItemGen.cleanup(posIrGoSpawner, instanceContainer);
            ItemGen.cleanup(posDiaSpawner, instanceContainer);
            return TaskSchedule.seconds(10);
        });

        CoreGame.createTeams();
        npcs = CoreGame.summonNPCs(instanceContainer);
        CoreGame.generateLobbySidebar();

        var lamp = MinestomLamp.builder().build();
        lamp.register(new AdminCommands());
        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> {
            sender.sendMessage(CoreGame.serverPrefix + "Unknown command: " + command);
        });

        MinecraftServer.setBrandName("BaedWars");
        minecraftServer.start("0.0.0.0", Integer.parseInt(config.getProperty("server-port", "25565")));
        float totalTime = (float) (System.nanoTime() - startTime) / 1000000000;
        logger.info(String.format(Locale.US, "Done (%.3fs)! For help, type \"help\"", totalTime));
    }
}