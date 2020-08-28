package net.okocraft.scoreboard.display.manager;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractDisplayManager implements DisplayManager {

    protected final ScoreboardPlugin plugin;
    protected final Set<BoardDisplay> displays;

    public AbstractDisplayManager(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
        this.displays = new HashSet<>();
    }

    @Override
    public void showBoard(@NotNull Player player, @NotNull Board board) {
        if (plugin.getServer().isPrimaryThread()) {
            BoardDisplay display = newDisplay(player, board);

            if (!display.isVisible()) {
                plugin.runAsync(display::showBoard);
            }

            plugin.runAsync(() -> add(display));
        } else {
            throw new IllegalStateException(Thread.currentThread().getName() + " is not primary thread.");
        }
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

            remove(display);
        }
    }

    @Override
    public void hideAllBoards() {
        displays.stream().filter(BoardDisplay::isVisible).forEach(BoardDisplay::hideBoard);
        clean();
    }

    @NotNull
    protected abstract BoardDisplay newDisplay(@NotNull Player player, @NotNull Board board);

    protected void add(@NotNull BoardDisplay display) {
        synchronized (displays) {
            displays.add(display);
        }
    }

    protected void remove(@NotNull BoardDisplay display) {
        synchronized (displays) {
            displays.remove(display);
        }
    }

    protected void clean() {
        synchronized (displays) {
            displays.clear();
        }
    }

    protected Optional<BoardDisplay> getDisplay(@NotNull Player player) {
        return displays.stream().filter(d -> d.getPlayer().equals(player)).findFirst();
    }
}
