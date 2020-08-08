package net.okocraft.scoreboard;

import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public class ScoreboardPlugin extends JavaPlugin {

    private ScoreboardManager scoreboardManager;
    private BoardManager boardManager;
    private PlayerListener listener;

    @Override
    public void onDisable() {
        if (boardManager != null) {
            boardManager.removeAll();
            boardManager = null;
        }

        if (listener != null) {
            listener.unregister();
            listener = null;
        }

        if (scoreboardManager != null) {
            scoreboardManager = null;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        try {
            boardManager = new BoardManager(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        listener = new PlayerListener(this);

        listener.register();

        getServer().getScheduler().runTaskLater(this, this::checkPlaceholderAPI, 1);
        getServer().getScheduler().runTaskLater(this, boardManager::showAllDefault, 2);
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
    public String checkLength(@NotNull String str) {
        int limit = Math.min(getConfig().getInt("board.limit", 32), 64);
        if (limit < ChatColor.stripColor(str).length()) {

            boolean bool = false;
            int colors = 0;
            int length = 0;

            for (char c : str.toCharArray()) {
                if (bool) {
                    if (-1 < "0123456789abcdefklmnor".indexOf(c)) {
                        colors += 2;
                    }
                    bool = false;
                    continue;
                }

                if (c == ChatColor.COLOR_CHAR) {
                    bool = true;
                }

                length++;

                if (limit < length) {
                    break;
                }
            }

            return str.substring(0, length + colors - 1);
        } else {
            return str;
        }
    }
}
