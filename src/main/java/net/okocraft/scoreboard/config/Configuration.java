package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import net.okocraft.scoreboard.ScoreboardPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class Configuration {

    private static final int DEFAULT_BOARD_LIMIT = 32;
    private static final int DEFAULT_THREADS = 5;

    private final YamlConfiguration config;

    public Configuration(@NotNull ScoreboardPlugin plugin) {
        config = YamlConfiguration.create(plugin.getDataFolder().toPath().resolve("config.yml"));

        try {
            ResourceUtils.copyFromClassLoaderIfNotExists(
                    plugin.getClass().getClassLoader(),
                    "config.yml",
                    config.getPath()
            );
            config.load();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load config.yml", e);
        }
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
