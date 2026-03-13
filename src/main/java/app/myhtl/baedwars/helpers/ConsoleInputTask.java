package app.myhtl.baedwars.helpers;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import net.minestom.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleInputTask implements Runnable {

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!MinecraftServer.getCommandManager().commandExists(line)) {
                    Server.logger.info(CoreGame.serverPrefixText + "This command doesn't exist!");
                }
                MinecraftServer.getCommandManager().executeServerCommand(line);
            }
        } catch (IOException e) {
            Server.logger.error(e.toString());
        }
    }
}
