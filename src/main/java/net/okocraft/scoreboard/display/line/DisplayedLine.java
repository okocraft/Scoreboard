package net.okocraft.scoreboard.display.line;

import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import net.okocraft.scoreboard.util.LengthChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayedLine {

    private final Player player;
    private final Line line;
    private final String teamName;
    private final String entryName;
    private String currentLine;
    private boolean changed = false;

    public DisplayedLine(@NotNull Player player, @NotNull Line line, int num) {
        this.player = player;
        this.line = line;
        this.teamName = String.valueOf(num);
        this.entryName = ChatColor.values()[num].toString();
        this.currentLine = PlaceholderAPIHooker.run(player, line.getCurrentLine());
    }

    public String getTeamName() {
        return teamName;
    }

    @NotNull
    public String getEntryName() {
        return entryName;
    }

    @NotNull
    public String getCurrentLine() {
        changed = false;
        return currentLine;
    }

    public void update() {
        if (line.shouldUpdate()) {
            String str = PlaceholderAPIHooker.run(player, line.getCurrentLine());
            str = LengthChecker.check(str);

            if (!currentLine.equals(str)) {
                currentLine = str;
                changed = true;
            }
        }
    }

    public boolean isChanged() {
        return changed;
    }
}
