package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import net.okocraft.scoreboard.ScoreboardPlugin;
import org.jetbrains.annotations.NotNull;

public class Configuration extends BukkitConfig {

    public Configuration(@NotNull ScoreboardPlugin plugin) {
        super(plugin, "config.yml", true);
    }

    public boolean isUsingProtocolLib() {
        return getBoolean("use-ProtocolLib", true);
    }

    public int getLengthLimit() {
        return Math.max(getInt("board.limit", 32), 1);
    }

    public int getThreads() {
        return Math.max(getInt("board.threads", 5), 1);
    }
}
