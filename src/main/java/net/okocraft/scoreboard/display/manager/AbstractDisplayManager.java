package net.okocraft.scoreboard.display.manager;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractDisplayManager implements DisplayManager {

    protected final ScoreboardPlugin plugin;
    private final Set<BoardDisplay> displays = new CopyOnWriteArraySet<>();

    public AbstractDisplayManager(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void showBoard(@NotNull Player player, @NotNull Board board) {
        var display = newDisplay(player, board);

        if (!display.isVisible()) {
            display.showBoard();
        }

        displays.add(display);
    }

    @Override
    public void showDefaultBoard(@NotNull Player player) {
        showBoard(player, plugin.getBoardManager().getDefaultBoard());
    }

    @Override
    public void hideBoard(@NotNull Player player) {
        Optional<BoardDisplay> optionalDisplay = getDisplay(player);

        if (optionalDisplay.isPresent()) {
            BoardDisplay display = optionalDisplay.get();

            if (display.isVisible()) {
                display.hideBoard();
            }

            displays.remove(display);
        }
    }

    @Override
    public void hideAllBoards() {
        displays.stream().filter(BoardDisplay::isVisible).forEach(BoardDisplay::hideBoard);
        displays.clear();
    }

    protected abstract @NotNull BoardDisplay newDisplay(@NotNull Player player, @NotNull Board board);

    private @NotNull Optional<BoardDisplay> getDisplay(@NotNull Player player) {
        return displays.stream().filter(d -> d.getPlayer().equals(player)).findFirst();
    }
}
