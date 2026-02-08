package app.myhtl.baedwars;

import app.myhtl.baedwars.commands.SkipLobby;
import app.myhtl.baedwars.handlers.blocks.Beehive;
import app.myhtl.baedwars.handlers.blocks.Chest;
import app.myhtl.baedwars.handlers.blocks.EnderChest;
import io.github.togar2.pvp.MinestomPvP;
import io.github.togar2.pvp.feature.CombatFeatureSet;
import io.github.togar2.pvp.feature.CombatFeatures;
import io.github.togar2.pvp.feature.FeatureType;
import io.github.togar2.pvp.feature.provider.DifficultyProvider;
import io.github.togar2.pvp.utils.CombatVersion;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;

import app.myhtl.baedwars.listeners.*;
import app.myhtl.baedwars.game.*;
import app.myhtl.baedwars.game.ItemGen;
import net.minestom.server.item.Material;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.UUID;

public class Server {
    public static Logger logger = LoggerFactory.getLogger(Server.class);
    public static boolean gameStarted = false;
    public static World map;
    public static NPC[] npcs;
    public static HashSet<BuyableItem> permanentItems = new HashSet<>();
    public static ShopCategory[] itemShopData = CoreGame.loadItemShopData();
    public static String round_id = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

    static {
        try {
            map = World.load(Path.of("worlds/map01.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    public static void main(String[] args) {
        // Initialize the server
        MinecraftServer minecraftServer = null;
        try {
            minecraftServer = MinecraftServer.init(new Auth.Velocity(Files.readString(Path.of("forwarding.secret"))));
        } catch (IOException e) {
            minecraftServer = MinecraftServer.init(new Auth.Online());
        }
        MinestomPvP.init();

        MinecraftServer.getBlockManager().registerHandler("minecraft:chest", Chest::new);
        MinecraftServer.getBlockManager().registerHandler("minecraft:ender_chest", EnderChest::new);
        MinecraftServer.getBlockManager().registerHandler("minecraft:beehive", Beehive::new);

        CombatFeatureSet modernVanilla = CombatFeatures.getVanilla(CombatVersion.MODERN, DifficultyProvider.DEFAULT)
                .remove(FeatureType.FOOD)
                .remove(FeatureType.EXHAUSTION)
                .build();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        // Add an event callback to specify the spawning instance (and the spawn position)
        instanceContainer.setChunkLoader(new AnvilLoader(map.savePath));
        instanceContainer.setExplosionSupplier(modernVanilla.get(FeatureType.EXPLOSION).getExplosionSupplier());
        instanceContainer.setTimeRate(0);
        var handler = MinecraftServer.getGlobalEventHandler();
        handler.addChild(modernVanilla.createNode());
        handler.addChild(AllEventListener.getPlayerEvent(instanceContainer, scheduler));
        handler.addChild(PreGameEventListener.getPlayerEvent(scheduler));
        handler.addChild(DeadEventListener.getPlayerEvent(scheduler));
        handler.addChild(VanillaEventListener.getPlayerEvent(scheduler));
        handler.addChild(BedwarsEventListener.getPlayerEvent(scheduler));
        handler.addChild(SpectatorEventListener.getPlayerEvent());

        Pos[] posIrGoSpawner = new Pos[]{new Pos(-56.5, 60.5, -69.5),new Pos(29.5, 60.5, 32.5)};
        Pos[] posDiaSpawner = new Pos[]{new Pos(-49.5, 61.5, -14.5), new Pos(22.5, 61.5, -22.5)};
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

        MinecraftServer.getCommandManager().register(new SkipLobby());
        MinecraftServer.setBrandName("BaedWars");
        minecraftServer.start("0.0.0.0", 25545);
    }
}