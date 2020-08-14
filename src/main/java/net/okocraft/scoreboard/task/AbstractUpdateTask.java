package net.okocraft.scoreboard.task;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.board.DisplayedBoard;
import net.okocraft.scoreboard.display.line.DisplayedLine;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractUpdateTask implements Runnable {

    private final ScoreboardPlugin plugin;
    private final AtomicBoolean replaced;

    protected final DisplayedBoard board;
    protected final DisplayedLine line;

    public AbstractUpdateTask(@NotNull ScoreboardPlugin plugin, @NotNull DisplayedBoard board, @NotNull DisplayedLine line) {
        this.plugin = plugin;
        this.board = board;
        this.line = line;
        this.replaced = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        line.update();

        replaced.set(false);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::replacePlaceholders);

        while (true) {
            if (replaced.get()) {
                break;
            }
        }

        line.checkLength();
        apply();
    }

    protected abstract void apply();

    private void replacePlaceholders() {
        line.replacePlaceholders();
        replaced.set(true);
    }
}
