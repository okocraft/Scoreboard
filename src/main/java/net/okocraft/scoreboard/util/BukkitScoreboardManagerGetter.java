package net.okocraft.scoreboard.util;

import org.bukkit.Server;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public final class BukkitScoreboardManagerGetter {

    private BukkitScoreboardManagerGetter() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static ScoreboardManager get(@NotNull Server server) throws IllegalStateException {
        ScoreboardManager scoreboardManager = server.getScoreboardManager();

        if (scoreboardManager == null) {
            throw new IllegalStateException("Could not get ScoreboardManager.");
        }

        return scoreboardManager;
    }
}
