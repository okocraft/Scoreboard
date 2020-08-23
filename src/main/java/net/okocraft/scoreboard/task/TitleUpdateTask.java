package net.okocraft.scoreboard.task;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.board.DisplayedBoard;
import org.jetbrains.annotations.NotNull;

public class TitleUpdateTask extends AbstractUpdateTask {

    public TitleUpdateTask(@NotNull ScoreboardPlugin plugin, @NotNull DisplayedBoard board) {
        super(plugin, board, board.getTitle());
    }

    @Override
    public void apply() {
        board.applyTitle();
    }
}
