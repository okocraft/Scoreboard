package net.okocraft.scoreboard.display.board;

import net.kyori.adventure.text.Component;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.task.UpdateTask;
import net.okocraft.scoreboard.util.scheduler.Task;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BukkitBoardDisplay implements BoardDisplay {

    private static final int MAX_LINES = 16;

    private final ScoreboardPlugin plugin;
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;

    private final LineDisplay title;
    private final List<LineDisplay> lines;

    private final List<Task> updateTasks = new ArrayList<>(MAX_LINES);

    public BukkitBoardDisplay(@NotNull ScoreboardPlugin plugin, @NotNull Board board,
                              @NotNull Player player, @NotNull Scoreboard scoreboard) {
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = scoreboard;

        this.title = new LineDisplay(player, board.getTitle(), 0);

        objective = scoreboard.registerNewObjective("sb", Criteria.DUMMY, title.getCurrentLine(), RenderType.INTEGER);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int size = Math.min(board.getLines().size(), MAX_LINES);
        var lines = new ArrayList<LineDisplay>(size);

        for (int i = 0; i < size; i++) {
            LineDisplay line = new LineDisplay(player, board.getLines().get(i), i);

            Team team = scoreboard.registerNewTeam(line.getName());

            var entryName = ENTRY_NAMES.get(i);

            team.addEntry(entryName);
            team.prefix(line.getCurrentLine());
            team.suffix(Component.empty());

            objective.getScore(entryName).setScore(size - i);

            lines.add(line);
        }

        this.lines = Collections.unmodifiableList(lines);
    }

    @Override
    public boolean isVisible() {
        return player.getScoreboard().equals(scoreboard);
    }

    @Override
    public void showBoard() {
        player.setScoreboard(scoreboard);
        scheduleUpdateTasks();
    }

    @Override
    public void hideBoard() {
        player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
        cancelUpdateTasks();
    }

    @Override
    public void applyTitle() {
        if (title.isChanged()) {
            objective.displayName(title.getCurrentLine());
        }
    }

    @Override
    public void applyLine(@NotNull LineDisplay line) {
        if (line.isChanged()) {
            Team team = scoreboard.getTeam(line.getName());

            if (team != null) {
                team.prefix(line.getCurrentLine());
            }
        }
    }

    @Override
    @NotNull
    public LineDisplay getTitle() {
        return title;
    }

    @Override
    @NotNull
    public List<LineDisplay> getLines() {
        return lines;
    }

    private void scheduleUpdateTasks() {
        if (getTitle().shouldUpdate()) {
            updateTasks.add(scheduleUpdateTask(getTitle(), true, getTitle().getInterval()));
        }

        for (LineDisplay line : getLines()) {
            if (line.shouldUpdate()) {
                updateTasks.add(scheduleUpdateTask(line, false, line.getInterval()));
            }
        }
    }

    private void cancelUpdateTasks() {
        updateTasks.forEach(Task::cancel);
        updateTasks.clear();
    }

    private Task scheduleUpdateTask(@NotNull LineDisplay display, boolean isTitleLine, long interval) {
        return plugin.getScheduler().scheduleUpdateTask(new UpdateTask(this, display, isTitleLine), interval);
    }
}
