package net.okocraft.scoreboard.display.board;

import net.kyori.adventure.text.Component;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.line.LineDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class BukkitBoardDisplay extends AbstractBoardDisplay {

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final LineDisplay title;
    private final List<LineDisplay> lines;

    public BukkitBoardDisplay(@NotNull ScoreboardPlugin plugin, @NotNull Board board,
                              @NotNull Player player, @NotNull Scoreboard scoreboard) {
        super(plugin, player);

        this.scoreboard = scoreboard;

        this.title = new LineDisplay(player, board.getTitle(), 0);

        objective = scoreboard.registerNewObjective("sb", "sb", title.getCurrentLine(), RenderType.INTEGER);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.lines = new LinkedList<>();

        for (int i = 0, l = board.getLines().size(); i < l && i < 16; i++) {
            LineDisplay line = new LineDisplay(player, board.getLines().get(i), i);

            Team team = scoreboard.registerNewTeam(line.getName());

            team.addEntry(line.getName());
            team.prefix(line.getCurrentLine());
            team.suffix(Component.empty());

            objective.getScore(line.getName()).setScore(l - i);
            lines.add(line);
        }
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
}
