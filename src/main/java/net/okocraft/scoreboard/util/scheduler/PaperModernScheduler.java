package net.okocraft.scoreboard.util.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import io.papermc.paper.util.Tick;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.task.UpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

final class PaperModernScheduler implements Scheduler {

    @Override
    public @NotNull Task runAsync(@NotNull Runnable task) {
        return createTaskFromScheduledTask(Bukkit.getAsyncScheduler().runNow(plugin(), $ -> task.run()));
    }

    @Override
    public @NotNull Task scheduleUpdateTask(@NotNull UpdateTask task, long tick) {
        long interval = Tick.of(tick).toMillis();
        return createTaskFromScheduledTask(Bukkit.getAsyncScheduler().runAtFixedRate(plugin(), $ -> task.run(), interval, interval, TimeUnit.MILLISECONDS));
    }

    @Override
    public void shutdown() {
        Bukkit.getAsyncScheduler().cancelTasks(plugin());
    }

    private @NotNull Task createTaskFromScheduledTask(@NotNull ScheduledTask scheduledTask) {
        return () -> {
            if (!scheduledTask.isCancelled()) {
                scheduledTask.cancel();
            }
        };
    }

    private @NotNull JavaPlugin plugin() {
        return JavaPlugin.getPlugin(ScoreboardPlugin.class);
    }
}
