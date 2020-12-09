package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.bukkit.BukkitYamlFactory;
import com.github.siroshun09.configapi.common.yaml.Yaml;
import net.okocraft.scoreboard.ScoreboardPlugin;
import org.jetbrains.annotations.NotNull;

public class Configuration {

    private static final int DEFAULT_BOARD_LIMIT = 32;
    private static final int DEFAULT_THREADS = 5;

    private final Yaml config;

    public Configuration(@NotNull ScoreboardPlugin plugin) {
        config = BukkitYamlFactory.loadUnsafe(plugin, "config.yml");
    }

    public boolean isUsingProtocolLib() {
        return config.getBoolean("use-ProtocolLib", true);
    }

    public int getLengthLimit() {
        return Math.max(config.getInteger("board.limit", DEFAULT_BOARD_LIMIT), 1);
    }

    public int getThreads() {
        return Math.max(config.getInteger("board.threads", DEFAULT_THREADS), 1);
    }
}
