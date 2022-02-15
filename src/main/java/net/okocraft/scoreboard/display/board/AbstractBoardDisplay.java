package net.okocraft.scoreboard.display.board;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.task.UpdateTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public abstract class AbstractBoardDisplay implements BoardDisplay {

    protected final ScoreboardPlugin plugin;
    protected final Player player;

    private final Set<ScheduledFuture<?>> updateTasks;

    public AbstractBoardDisplay(@NotNull ScoreboardPlugin plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        updateTasks = new HashSet<>();
    }

    @Override
    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Override
    public void scheduleUpdateTasks() {
        if (getTitle().shouldUpdate()) {
            updateTasks.add(
                    plugin.scheduleUpdateTask(new UpdateTask(this, getTitle(), true),
                            getTitle().getInterval())
            );
        }

        for (LineDisplay line : getLines()) {
            if (line.shouldUpdate()) {
                updateTasks.add(
                        plugin.scheduleUpdateTask(new UpdateTask(this, line, false),
                                line.getInterval())
                );
            }
        }
    }

    @Override
    public void cancelUpdateTasks() {
        updateTasks.stream().filter(t -> !t.isCancelled()).forEach(t -> t.cancel(true));
        updateTasks.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof AbstractBoardDisplay that) {
            return player.equals(that.player);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
