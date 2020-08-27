package net.okocraft.scoreboard.display.board;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.line.LineDisplay;
import org.bukkit.ChatColor;
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

    public BukkitBoardDisplay(@NotNull ScoreboardPlugin plugin, @NotNull Board board, @NotNull Player player) {
        super(plugin, player);

        scoreboard = plugin.getScoreboardManager().getNewScoreboard();

        this.title = new LineDisplay(player, board.getTitle(), 0);

        objective = scoreboard.registerNewObjective("sb", "sb", title.getCurrentLine(), RenderType.INTEGER);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.lines = new LinkedList<>();

        for (int i = 0, l = board.getLines().size(), c = ChatColor.values().length; i < l && i < c; i++) {
            LineDisplay line = new LineDisplay(player, board.getLines().get(i), i);

            Team team = scoreboard.registerNewTeam(line.getTeamName());

            team.addEntry(line.getEntryName());
            team.setPrefix(line.getCurrentLine());
            team.setSuffix(ChatColor.RESET.toString());

            objective.getScore(line.getEntryName()).setScore(l - i);
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
        player.setScoreboard(plugin.getScoreboardManager().getMainScoreboard());
        cancelUpdateTasks();
    }

    @Override
    public void applyTitle() {
        if (title.isChanged()) {
            objective.setDisplayName(title.getCurrentLine());
        }
    }

    @Override
    public void applyLine(@NotNull LineDisplay line) {
        if (line.isChanged()) {
            Team team = scoreboard.getTeam(line.getTeamName());

            if (team != null) {
                team.setPrefix(line.getCurrentLine());
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
