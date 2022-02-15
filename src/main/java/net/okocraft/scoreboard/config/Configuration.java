package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.util.LengthChecker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class Configuration {

    private static final int DEFAULT_BOARD_LIMIT = 32;
    private static final int DEFAULT_THREADS = 5;

    private final ScoreboardPlugin plugin;

    private int threads = DEFAULT_THREADS;

    public Configuration(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    public int getThreads() {
        return threads;
    }

    public void reload() {
        var config = YamlConfiguration.create(plugin.getDataFolder().toPath().resolve("config.yml"));

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

        threads = Math.max(config.getInteger("board.threads", DEFAULT_THREADS), 1);
        LengthChecker.setLimit(Math.max(config.getInteger("board.limit", DEFAULT_BOARD_LIMIT), 1));

        config.close();
    }
}
