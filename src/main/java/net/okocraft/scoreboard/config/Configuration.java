package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import net.okocraft.scoreboard.ScoreboardPlugin;
import org.jetbrains.annotations.NotNull;

public class Configuration {

    private static final int DEFAULT_BOARD_LIMIT = 32;
    private static final int DEFAULT_THREADS = 5;

    private final BukkitConfig config;

    public Configuration(@NotNull ScoreboardPlugin plugin) {
        config = new BukkitConfig(plugin, "config.yml", true);
    }

    public boolean isUsingProtocolLib() {
        return config.getBoolean("use-ProtocolLib", true);
    }

    public int getLengthLimit() {
        return Math.max(config.getInt("board.limit", DEFAULT_BOARD_LIMIT), 1);
    }

    public int getThreads() {
        return Math.max(config.getInt("board.threads", DEFAULT_THREADS), 1);
    }
}
