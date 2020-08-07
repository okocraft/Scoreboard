package net.okocraft.scoreboard.board;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Board {

    private final Line title;
    private final List<Line> lines;

    public Board(@NotNull Line title, @NotNull List<Line> lines) {
        this.title = title;
        this.lines = lines;
    }

    public void update(long increment) {
        if (title.shouldUpdate()) {
            title.update(increment);
        }

        lines.stream().filter(Line::shouldUpdate).forEach(l -> l.update(increment));
    }

    @NotNull
    public Line getTitle() {
        return title;
    }

    @NotNull
    public List<Line> getLines() {
        return lines;
    }
}
