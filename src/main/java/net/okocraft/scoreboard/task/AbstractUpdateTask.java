package net.okocraft.scoreboard.task;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractUpdateTask implements UpdateTask {

    private final ScoreboardPlugin plugin;

    protected final BoardDisplay board;
    protected final LineDisplay line;

    public AbstractUpdateTask(@NotNull ScoreboardPlugin plugin, @NotNull BoardDisplay board, @NotNull LineDisplay line) {
        this.plugin = plugin;
        this.board = board;
        this.line = line;
    }

    @Override
    public void run() {
        line.update();

        if (PlaceholderAPIHooker.isEnabled() && line.hasPlaceholders()) {
            AtomicBoolean waiting = new AtomicBoolean(false);
            AtomicBoolean replaced = new AtomicBoolean(false);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                line.replacePlaceholders();
                replaced.set(true);

                if (waiting.get()) {
                    synchronized (waiting) {
                        waiting.notify();
                    }
                }
            });

            if (!replaced.get()) {
                waiting.set(true);

                synchronized (waiting) {
                    try {
                        waiting.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        line.checkLength();
        apply();
    }
}
