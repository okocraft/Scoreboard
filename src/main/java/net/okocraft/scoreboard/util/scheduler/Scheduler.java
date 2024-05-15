package net.okocraft.scoreboard.util.scheduler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.okocraft.scoreboard.task.UpdateTask;
import org.jetbrains.annotations.NotNull;

public interface Scheduler {

    static @NotNull Scheduler create() {
        return new PaperModernScheduler();
    }

    @CanIgnoreReturnValue
    @NotNull Task runAsync(@NotNull Runnable task);

    @CanIgnoreReturnValue
    @NotNull Task scheduleUpdateTask(@NotNull UpdateTask task, long tick);

    void shutdown();

}
