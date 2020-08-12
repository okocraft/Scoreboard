package net.okocraft.scoreboard.player;

import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.papi.PlaceholderAPIHooker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiaplayedLine {

    private final Player player;
    private final Line line;
    private String currentLine;

    public DiaplayedLine(@NotNull Player player, @NotNull Line line) {
        this.player = player;
        this.line = line;
        this.currentLine = PlaceholderAPIHooker.run(player, line.getCurrentLine());
    }

    @NotNull
    public String getCurrentLine() {
        return currentLine;
    }

    public void update() {
        if (line.shouldUpdate()) {
            currentLine = PlaceholderAPIHooker.run(player, line.getCurrentLine());
        }
    }
}
