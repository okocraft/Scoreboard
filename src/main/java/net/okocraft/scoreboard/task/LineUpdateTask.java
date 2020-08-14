package net.okocraft.scoreboard.task;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.board.DisplayedBoard;
import net.okocraft.scoreboard.display.line.DisplayedLine;
import org.jetbrains.annotations.NotNull;

public class LineUpdateTask extends AbstractUpdateTask {

    public LineUpdateTask(@NotNull ScoreboardPlugin plugin, @NotNull DisplayedBoard board, @NotNull DisplayedLine line) {
        super(plugin, board, line);
    }

    @Override
    protected void apply() {
        board.applyLine(line);
    }
}
