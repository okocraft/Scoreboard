package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import net.okocraft.scoreboard.ScoreboardPlugin;
import org.jetbrains.annotations.NotNull;

public class Configuration {

    private final BukkitConfig config;

    public Configuration(@NotNull ScoreboardPlugin plugin) {
        config = new BukkitConfig(plugin, "config.yml", true);
    }

    public boolean isUsingProtocolLib() {
        return config.getBoolean("use-ProtocolLib", true);
    }

    public int getLengthLimit() {
        return Math.max(config.getInt("board.limit", 32), 1);
    }

    public int getThreads() {
        return Math.max(config.getInt("board.threads", 5), 1);
    }
}
