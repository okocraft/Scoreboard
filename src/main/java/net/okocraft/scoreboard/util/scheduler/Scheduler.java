package net.okocraft.scoreboard.util.scheduler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.okocraft.scoreboard.task.UpdateTask;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public interface Scheduler {

    static @NotNull Scheduler create() {
        try {
            Bukkit.class.getDeclaredMethod("getAsyncScheduler");
            return new PaperModernScheduler();
        } catch (NoSuchMethodException e) {
            return new JavaScheduler();
        }
    }

    @CanIgnoreReturnValue
    @NotNull Task runAsync(@NotNull Runnable task);

    @CanIgnoreReturnValue
    @NotNull Task scheduleUpdateTask(@NotNull UpdateTask task, long tick);

    void shutdown();

}
