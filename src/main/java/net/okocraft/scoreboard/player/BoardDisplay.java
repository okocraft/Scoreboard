package net.okocraft.scoreboard.player;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BoardDisplay {

    private final ScoreboardPlugin plugin;
    private final Board board;
    private final Player player;

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final Map<Integer, String> currentLines;

    private String currentTitle = "...";

    public BoardDisplay(@NotNull ScoreboardPlugin plugin, @NotNull Board board, @NotNull Player player) {
        this.plugin = plugin;
        this.board = board;
        this.player = player;

        scoreboard = plugin.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("sb", "sb", currentTitle, RenderType.INTEGER);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        currentLines = new HashMap<>();

        for (int i = 0, l = board.getLines().size(), c = ChatColor.values().length; i < l && i < c; i++) {
            String name = ChatColor.values()[i] + "";
            scoreboard.registerNewTeam(String.valueOf(i)).addEntry(name);
            objective.getScore(name).setScore(l - i);
        }

        player.setScoreboard(scoreboard);
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public void update() {
        updateTitle();
        updateLines();
    }

    public void updateTitle() {
        String newTitle = PlaceholderAPIHooker.run(player, board.getTitle().getCurrentLine());

        if (!currentTitle.equals(newTitle)) {
            currentTitle = newTitle;
            objective.setDisplayName(plugin.checkLength(newTitle));
        }
    }

    public void updateLines() {
        for (int i = 0, l = board.getLines().size(); i < l; i++) {
            updateLine(i);
        }
    }

    private void updateLine(int num) {
        if (board.getLines().size() <= num) {
            return;
        }

        Team team = scoreboard.getTeam(String.valueOf(num));

        if (team == null) {
            return;
        }

        Line line = board.getLines().get(num);

        String str = PlaceholderAPIHooker.run(player, line.getCurrentLine());

        if (currentLines.getOrDefault(num, "").equals(str)) {
            return;
        }

        currentLines.put(num, str);

        team.setPrefix(plugin.checkLength(str));
        team.setSuffix(ChatColor.RESET.toString());
    }
}
