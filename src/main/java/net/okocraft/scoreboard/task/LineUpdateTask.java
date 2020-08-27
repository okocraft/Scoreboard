package net.okocraft.scoreboard.task;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import net.okocraft.scoreboard.display.line.LineDisplay;
import org.jetbrains.annotations.NotNull;

public class LineUpdateTask extends AbstractUpdateTask {

    public LineUpdateTask(@NotNull ScoreboardPlugin plugin, @NotNull BoardDisplay board, @NotNull LineDisplay line) {
        super(plugin, board, line);
    }

    @Override
    public void apply() {
        board.applyLine(line);
    }
}
