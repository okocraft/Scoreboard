package net.okocraft.scoreboard.display.manager;

import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.config.BoardManager;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import net.okocraft.scoreboard.display.board.BoardDisplayProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BoardDisplayManager {

    private final BoardManager boardManager;
    private final BoardDisplayProvider displayProvider;

    private final Map<UUID, BoardDisplay> displayMap = new ConcurrentHashMap<>();

    public BoardDisplayManager(@NotNull BoardManager boardManager, @NotNull BoardDisplayProvider displayProvider) {
        this.boardManager = boardManager;
        this.displayProvider = displayProvider;
    }

    public void showBoard(@NotNull Player player, @NotNull Board board) {
        this.hideBoard(player);

        var display = this.displayProvider.newDisplay(player, board);

        if (!display.isVisible()) {
            display.showBoard();
        }

        this.displayMap.put(player.getUniqueId(), display);
    }

    public void showDefaultBoard(@NotNull Player player) {
        this.showBoard(player, this.boardManager.getDefaultBoard());
    }

    public void hideBoard(@NotNull Player player) {
        var display = this.displayMap.remove(player.getUniqueId());

        if (display != null && display.isVisible()) {
            display.hideBoard();
        }
    }

    public void hideAllBoards() {
        this.displayMap.values().stream().filter(BoardDisplay::isVisible).forEach(BoardDisplay::hideBoard);
        this.displayMap.clear();
    }

    public boolean isDisplayed(@NotNull Player player) {
        return this.displayMap.containsKey(player.getUniqueId());
    }

}
