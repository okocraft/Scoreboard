package net.okocraft.scoreboard.board;

import com.github.siroshun09.textlibs.util.Colorizer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Line {
    public final static Line EMPTY = new Line(Collections.emptyList(), 0);

    private final List<String> lines;
    private final long interval;
    private final boolean shouldUpdate;

    private String currentLine = "";
    private int currentIndex = 0;
    private long count = 0;

    public Line(@NotNull List<String> lines, long interval) {
        this.lines = List.copyOf(lines);
        this.shouldUpdate = 0 < interval;
        this.interval = interval;

        if (!lines.isEmpty()) {
            currentLine = Colorizer.colorize(lines.get(0));
        }
    }

    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    public long getInterval() {
        return interval;
    }

    public void update(long increment) {
        if (interval <= count) {
            count = 0;

            currentIndex++;

            if (lines.size() <= currentIndex) {
                currentIndex = 0;
            }

            currentLine = Colorizer.colorize(lines.get(currentIndex));
        } else {
            count += increment;
        }
    }

    @NotNull
    public String getCurrentLine() {
        return currentLine;
    }
}
