package net.okocraft.scoreboard.display.board;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.line.DisplayedLine;
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

public class BukkitDisplayedBoard extends AbstractDisplayedBoard {

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final DisplayedLine title;
    private final List<DisplayedLine> lines;

    public BukkitDisplayedBoard(@NotNull ScoreboardPlugin plugin, @NotNull Board board, @NotNull Player player) {
        super(plugin, player);

        scoreboard = plugin.getScoreboardManager().getNewScoreboard();

        this.title = new DisplayedLine(player, board.getTitle(), 0);

        objective = scoreboard.registerNewObjective("sb", "sb", title.getCurrentLine(), RenderType.INTEGER);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<DisplayedLine> lines = new LinkedList<>();

        for (int i = 0, l = board.getLines().size(), c = ChatColor.values().length; i < l && i < c; i++) {
            DisplayedLine line = new DisplayedLine(player, board.getLines().get(i), i);

            Team team = scoreboard.registerNewTeam(line.getTeamName());

            team.addEntry(line.getEntryName());
            team.setPrefix(line.getCurrentLine());
            team.setSuffix(ChatColor.RESET.toString());

            objective.getScore(line.getEntryName()).setScore(l - i);
            lines.add(line);
        }

        this.lines = List.copyOf(lines);

        player.setScoreboard(scoreboard);
    }

    @Override
    public void applyTitle() {
        if (title.isChanged()) {
            objective.setDisplayName(title.getCurrentLine());
        }
    }

    @Override
    public void applyLine(@NotNull DisplayedLine line) {
        if (line.isChanged()) {
            Team team = scoreboard.getTeam(line.getTeamName());

            if (team != null) {
                team.setPrefix(line.getCurrentLine());
            }
        }
    }

    @Override
    @NotNull
    public DisplayedLine getTitle() {
        return title;
    }

    @Override
    @NotNull
    public List<DisplayedLine> getLines() {
        return lines;
    }
}
