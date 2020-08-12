package net.okocraft.scoreboard.player;

import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.Scoreboard;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DisplayedBoard {

    private final ScoreboardPlugin plugin;
    private final Player player;

    private final DiaplayedLine title;
    private final List<DiaplayedLine> lines;

    private final ScoreboardObjective objective;

    public DisplayedBoard(@NotNull ScoreboardPlugin plugin, @NotNull Board board, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;

        this.title = new DiaplayedLine(player, board.getTitle());
        this.lines = board.getLines().stream().map(l -> new DiaplayedLine(player, l)).collect(Collectors.toList());

        objective = Services.load(Scoreboard.class).createPlayerObjective(player, "...", DisplaySlot.SIDEBAR, false);

        plugin.runAsync(this::apply);
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public void update() {
        title.update();
        lines.forEach(DiaplayedLine::update);

        plugin.runAsync(this::apply);
    }

    public void apply() {
        objective.setDisplayName(plugin.checkLength(title.getCurrentLine()));

        objective.applyLines(
                lines.stream().map(DiaplayedLine::getCurrentLine).map(plugin::checkLength).collect(Collectors.toList())
        );
    }
}
