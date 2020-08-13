package net.okocraft.scoreboard;

import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import net.okocraft.scoreboard.util.LengthChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreboardPlugin extends JavaPlugin {

    private ScoreboardManager scoreboardManager;
    private BoardManager boardManager;
    private PlayerListener listener;
    private ExecutorService executor;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        executor = Executors.newCachedThreadPool();

        try {
            boardManager = new BoardManager(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LengthChecker.setLimit(Math.max(getConfig().getInt("board.limit", 32), 1));

        listener = new PlayerListener(this);
        listener.register();

        getServer().getScheduler().runTaskLater(this, this::checkPlaceholderAPI, 1);
        getServer().getScheduler().runTaskLater(this, boardManager::showAllDefault, 2);
    }

    @Override
    public void onDisable() {
        if (boardManager != null) {
            boardManager.removeAll();
        }

        if (listener != null) {
            listener.unregister();
        }

        if (executor != null) {
            executor.shutdownNow();
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

    private void checkPlaceholderAPI() {
        PlaceholderAPIHooker.checkEnabled();

        if (PlaceholderAPIHooker.isEnabled()) {
            getLogger().info("PlaceholderAPI is available!");
        }
    }

    @NotNull
    public ExecutorService getExecutor() {
        return executor;
    }

    boolean isUsingProtocolLib() {
        return getConfig().getBoolean("use-ProtocolLib", true) &&
                getServer().getPluginManager().getPlugin("ProtocolLib") != null;
    }
}
