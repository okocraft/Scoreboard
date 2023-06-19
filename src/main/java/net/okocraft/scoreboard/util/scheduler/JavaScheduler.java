package net.okocraft.scoreboard.util.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.task.UpdateTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

final class JavaScheduler implements Scheduler {

    private static final long MILLISECONDS_PER_TICK = 50;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3, createThreadFactory());

    @Override
    public @NotNull Task runAsync(@NotNull Runnable task) {
        return createTaskFromFuture(scheduler.submit(task));
    }

    @Override
    public @NotNull Task scheduleUpdateTask(@NotNull UpdateTask task, long tick) {
        long interval = tick * MILLISECONDS_PER_TICK;
        return createTaskFromFuture(scheduler.scheduleAtFixedRate(task, interval, interval, TimeUnit.MILLISECONDS));
    }

    @Override
    public void shutdown() {
        scheduler.shutdownNow();
    }

    private @NotNull Task createTaskFromFuture(@NotNull Future<?> future) {
        return () -> {
            if (!future.isCancelled() && !future.isDone()) {
                future.cancel(true);
            }
        };
    }

    private @NotNull ThreadFactory createThreadFactory() {
        return new ThreadFactoryBuilder()
                .setNameFormat("Scoreboard Thread - %d")
                .setUncaughtExceptionHandler(this::catchUncaughtException)
                .setDaemon(true)
                .build();
    }

    private void catchUncaughtException(Thread thread, Throwable exception) {
        JavaPlugin.getPlugin(ScoreboardPlugin.class).getLogger().log(
                Level.SEVERE,
                "An exception occurred on " + thread.getName(),
                exception
        );
    }
}
