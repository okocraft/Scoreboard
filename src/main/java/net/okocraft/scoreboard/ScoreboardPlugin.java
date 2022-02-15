package net.okocraft.scoreboard;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.translationloader.directory.TranslationDirectory;
import net.kyori.adventure.key.Key;
import net.okocraft.scoreboard.command.ScoreboardCommand;
import net.okocraft.scoreboard.config.BoardManager;
import net.okocraft.scoreboard.config.Configuration;
import net.okocraft.scoreboard.display.manager.BukkitDisplayManager;
import net.okocraft.scoreboard.display.manager.DisplayManager;
import net.okocraft.scoreboard.external.PlaceholderAPIHooker;
import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.listener.PluginListener;
import net.okocraft.scoreboard.task.UpdateTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScoreboardPlugin extends JavaPlugin {

    private static final long MILLISECONDS_PER_TICK = 50;

    private static ScoreboardPlugin INSTANCE;

    public static @NotNull Plugin getPlugin() {
        return Objects.requireNonNull(INSTANCE);
    }

    private final TranslationDirectory translationDirectory =
            TranslationDirectory.newBuilder()
                    .setKey(Key.key("scoreboard:languages"))
                    .setDirectory(getDataFolder().toPath().resolve("languages"))
                    .setDefaultLocale(Locale.ENGLISH)
                    .onDirectoryCreated(this::saveDefaultLanguages)
                    .build();

    private Configuration config;

    private BoardManager boardManager;
    private DisplayManager displayManager;
    private PlayerListener playerListener;
    private PluginListener pluginListener;

    private ExecutorService executor;
    private ScheduledExecutorService scheduler;

    @Override
    public void onLoad() {
        INSTANCE = this;

        try {
            translationDirectory.load();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load languages", e);
        }

        config = new Configuration(this);

        executor = Executors.newFixedThreadPool(config.getThreads());
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onEnable() {
        boardManager = new BoardManager(this);

        playerListener = new PlayerListener(this);
        playerListener.register();

        pluginListener = new PluginListener(this);
        pluginListener.register();

        displayManager = new BukkitDisplayManager(this);

        if (PlaceholderAPIHooker.checkEnabled(getServer())) {
            printPlaceholderIsAvailable();
        }

        var command = getCommand("sboard");

        if (command != null) {
            var impl = new ScoreboardCommand(this);
            command.setExecutor(impl);
            command.setTabCompleter(impl);
        }

        runAsync(() -> getServer().getOnlinePlayers().forEach(displayManager::showDefaultBoard));
    }

    @Override
    public void onDisable() {
        if (displayManager != null) {
            displayManager.hideAllBoards();
        }

        if (playerListener != null) {
            playerListener.unregister();
        }

        if (pluginListener != null) {
            pluginListener.unregister();
        }

        if (executor != null) {
            executor.shutdownNow();
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    public void reload() {
        displayManager.hideAllBoards();

        try {
            translationDirectory.load();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load languages", e);
        }

        config.reload();
        boardManager.reload();

        runAsync(() -> getServer().getOnlinePlayers().forEach(displayManager::showDefaultBoard));
    }

    @NotNull
    public BoardManager getBoardManager() {
        if (boardManager == null) {
            throw new IllegalStateException();
        }

        return boardManager;
    }

    public DisplayManager getDisplayManager() {
        if (boardManager == null) {
            throw new IllegalStateException();
        }

        return displayManager;
    }

    public void runAsync(@NotNull Runnable runnable) {
        executor.submit(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                getLogger().log(Level.SEVERE, null, e);
            }
        });
    }

    @NotNull
    public ScheduledFuture<?> scheduleUpdateTask(@NotNull UpdateTask task, long tick) {
        long interval = tick * MILLISECONDS_PER_TICK;
        return scheduler.scheduleWithFixedDelay(() -> runAsync(task), interval, interval, TimeUnit.MILLISECONDS);
    }

    public void printPlaceholderIsAvailable() {
        getLogger().info("PlaceholderAPI is available!");
    }

    private void saveDefaultLanguages(@NotNull Path directory) throws IOException {
        var english = "en.yml";
        ResourceUtils.copyFromJarIfNotExists(getFile().toPath(), english, directory.resolve(english));

        var japanese = "ja_JP.yml";
        ResourceUtils.copyFromJarIfNotExists(getFile().toPath(), japanese, directory.resolve(japanese));
    }
}
