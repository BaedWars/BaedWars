package app.myhtl.baedwars;

import app.myhtl.baedwars.commands.SkipLobby;
import io.github.togar2.pvp.MinestomPvP;
import io.github.togar2.pvp.feature.CombatFeatureSet;
import io.github.togar2.pvp.feature.CombatFeatures;
import io.github.togar2.pvp.feature.FeatureType;
import io.github.togar2.pvp.feature.provider.DifficultyProvider;
import io.github.togar2.pvp.utils.CombatVersion;
import net.kyori.adventure.nbt.CompoundBinaryTag;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Server {
    public static boolean gameStarted = false;
    public static World map;
    public static NPC[] npcs;
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
        var handler = MinecraftServer.getGlobalEventHandler();
        handler.addChild(modernVanilla.createNode());
        handler.addChild(AllEventListener.getPlayerEvent(instanceContainer, scheduler));
        handler.addChild(PreGameEventListener.getPlayerEvent(scheduler));
        handler.addChild(DeadEventListener.getPlayerEvent(scheduler));
        handler.addChild(VanillaEventListener.getPlayerEvent(scheduler));
        handler.addChild(BedwarsEventListener.getPlayerEvent(scheduler));
        handler.addChild(SpectatorEventListener.getPlayerEvent());

        Material[] materials = new Material[]{Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND};
        Pos[] posIrGoSpawner = new Pos[]{new Pos(-56.5, 60.5, -69.5),new Pos(29.5, 60.5, 32.5)};
        scheduler.submitTask(() -> {
            ItemGen.item(posIrGoSpawner, instanceContainer, materials[0]);
            return TaskSchedule.millis(500);
        });
        scheduler.submitTask(() -> {
            ItemGen.item(posIrGoSpawner, instanceContainer, materials[1]);
            return TaskSchedule.seconds(3);
        });
        // Register Events (set spawn instance, teleport player at spawn)
        // Start the server
        CoreGame.createTeams();
        npcs = CoreGame.summonNPCs(instanceContainer);
        CoreGame.generateLobbySidebar();
        MinecraftServer.getCommandManager().register(new SkipLobby());
        minecraftServer.start("0.0.0.0", 25545);
        System.out.println("Minecraft Server started");
    }
}