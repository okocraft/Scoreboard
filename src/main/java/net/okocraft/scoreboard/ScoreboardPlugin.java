package net.okocraft.scoreboard;

import net.okocraft.scoreboard.config.BoardManager;
import net.okocraft.scoreboard.config.Configuration;
import net.okocraft.scoreboard.display.manager.BukkitDisplayManager;
import net.okocraft.scoreboard.display.manager.DisplayManager;
import net.okocraft.scoreboard.external.PlaceholderAPIHooker;
import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.listener.PluginListener;
import net.okocraft.scoreboard.task.UpdateTask;
import net.okocraft.scoreboard.util.LengthChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScoreboardPlugin extends JavaPlugin {

    private static final long MILLISECONDS_PER_TICK = 50;

    private Configuration config;

    private BoardManager boardManager;
    private DisplayManager displayManager;
    private PlayerListener playerListener;
    private PluginListener pluginListener;

    private ExecutorService executor;
    private ScheduledExecutorService scheduler;

    @Override
    public void onLoad() {
        config = new Configuration(this);

        LengthChecker.setLimit(config.getLengthLimit());

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
}
