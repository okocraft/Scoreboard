package net.okocraft.scoreboard;

import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.listener.PluginListener;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import net.okocraft.scoreboard.task.UpdateTask;
import net.okocraft.scoreboard.util.LengthChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScoreboardPlugin extends JavaPlugin {

    private ScoreboardManager scoreboardManager;
    private BoardManager boardManager;
    private PlayerListener playerListener;
    private PluginListener pluginListener;
    private ExecutorService executor;
    private ScheduledExecutorService scheduler;
    private boolean useProtocolLib;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        executor = Executors.newFixedThreadPool(Math.max(1, getConfig().getInt("board.threads", 5)));
        scheduler = Executors.newSingleThreadScheduledExecutor();

        try {
            boardManager = new BoardManager(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LengthChecker.setLimit(Math.max(getConfig().getInt("board.limit", 32), 1));

        playerListener = new PlayerListener(this);
        playerListener.register();

        pluginListener = new PluginListener(this);
        pluginListener.register();

        getServer().getScheduler().runTaskLater(this, this::checkPlaceholderAPI, 1);
        getServer().getScheduler().runTaskLater(this, this::checkProtocolLib, 1);
        getServer().getScheduler().runTaskLater(this, boardManager::showAllDefault, 2);
    }

    @Override
    public void onDisable() {
        if (boardManager != null) {
            boardManager.removeAll();
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
    public ScoreboardManager getScoreboardManager() {
        if (scoreboardManager == null) {
            scoreboardManager = getServer().getScoreboardManager();
        }

        if (scoreboardManager == null) {
            throw new IllegalStateException();
        }

        return scoreboardManager;
    }

    @NotNull
    public BoardManager getBoardManager() {
        if (boardManager == null) {
            throw new IllegalStateException();
        }

        return boardManager;
    }

    @NotNull
    public ExecutorService getExecutor() {
        return executor;
    }

    @NotNull
    public ScheduledFuture<?> scheduleUpdateTask(@NotNull UpdateTask task, long tick) {
        long interval = tick * 50;
        return scheduler.scheduleWithFixedDelay(() -> executor.submit(task), interval, interval, TimeUnit.MILLISECONDS);
    }

    public void checkPlaceholderAPI() {
        PlaceholderAPIHooker.setEnabled(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null);

        if (PlaceholderAPIHooker.isEnabled()) {
            getLogger().info("PlaceholderAPI is available!");
        }
    }

    public void checkProtocolLib() {
        useProtocolLib = getServer().getPluginManager().getPlugin("ProtocolLib") != null && getConfig().getBoolean("use-ProtocolLib", true);

        if (useProtocolLib) {
            getLogger().info("We are using ProtocolLib.");
        } else {
            getLogger().info("We are using Bukkit's Scoreboard.");
        }

        boardManager.removeAll();
        boardManager.showAllDefault();
    }

    public boolean isUsingProtocolLib() {
        return useProtocolLib;
    }
}
