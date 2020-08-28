package net.okocraft.scoreboard.config;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BoardManager {

    private final ScoreboardPlugin plugin;

    private Board defaultBoard;
    private Set<Board> customBoards;

    public BoardManager(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    @NotNull
    public Board getDefaultBoard() {
        return defaultBoard;
    }

    @NotNull
    public Set<Board> getCustomBoards() {
        return customBoards;
    }

    public void reload() throws IllegalStateException {
        defaultBoard = BoardLoader.loadDefaultBoard(plugin);
        customBoards = BoardLoader.loadCustomBoards(plugin);
    }
}
