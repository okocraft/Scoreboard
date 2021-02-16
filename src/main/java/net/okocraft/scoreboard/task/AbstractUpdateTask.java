package net.okocraft.scoreboard.task;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.external.PlaceholderAPIHooker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractUpdateTask implements UpdateTask {

    private static final long MILLISECONDS_PER_SECONDS = TimeUnit.SECONDS.toMillis(1);

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

        if (line.hasPlaceholders()) {
            line.replacePlaceholders();
        }

        if (PlaceholderAPIHooker.isEnabled() && line.hasPlaceholders()) {
            var waiting = new AtomicBoolean();

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                    plugin,
                    () -> {
                        line.runPlaceholderApi();

                        synchronized (waiting) {
                            if (waiting.get()) {
                                waiting.notifyAll();
                            }
                        }
                    }
            );

            synchronized (waiting) {
                try {
                    waiting.set(true);
                    waiting.wait(MILLISECONDS_PER_SECONDS);
                } catch (InterruptedException ignored) {
                }
            }
        }

        line.checkLength();
        apply();
    }
}
