package net.okocraft.scoreboard.display.line;

import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import net.okocraft.scoreboard.util.Colorizer;
import net.okocraft.scoreboard.util.LengthChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayedLine {

    private final Player player;
    private final Line line;
    private final String teamName;
    private final String entryName;

    private String prevLine;
    private String currentLine;

    private int currentIndex = 0;

    public DisplayedLine(@NotNull Player player, @NotNull Line line, int num) {
        this.player = player;
        this.line = line;
        this.teamName = String.valueOf(num);
        this.entryName = ChatColor.values()[num].toString();
        this.currentLine = line.isEmpty() ? "" : PlaceholderAPIHooker.run(player, Colorizer.colorize(line.get(0)));
        checkLength();
    }

    @NotNull
    public String getTeamName() {
        return teamName;
    }

    @NotNull
    public String getEntryName() {
        return entryName;
    }

    @NotNull
    public String getCurrentLine() {
        return currentLine;
    }

    public void update() {
        currentIndex++;

        if (line.getMaxIndex() < currentIndex) {
            currentIndex = 0;
        }

        currentLine = Colorizer.colorize(line.get(currentIndex));
    }

    public void replacePlaceholders() {
        currentLine = PlaceholderAPIHooker.run(player, currentLine);
    }

    public void checkLength() {
        currentLine = LengthChecker.check(currentLine);
    }

    public boolean isChanged() {
        if (currentLine.equals(prevLine)) {
            return false;
        } else {
            prevLine = currentLine;
            return true;
        }
    }

    public boolean shouldUpdate() {
        return line.shouldUpdate();
    }

    public long getInterval() {
        return line.getInterval();
    }
}
