package net.okocraft.scoreboard.external;

import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class ProtocolLibChecker {

    private ProtocolLibChecker() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkEnabled(@NotNull Server server) {
        return server.getPluginManager().getPlugin("ProtocolLib") != null;
    }
}
