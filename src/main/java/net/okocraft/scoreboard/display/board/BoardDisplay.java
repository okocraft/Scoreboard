package net.okocraft.scoreboard.display.board;

import net.okocraft.scoreboard.display.line.DisplayedLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BoardDisplay {

    @NotNull
    Player getPlayer();

    @NotNull
    DisplayedLine getTitle();

    @NotNull
    List<DisplayedLine> getLines();

    boolean isVisible();

    void showBoard();

    void hideBoard();

    void applyTitle();

    void applyLine(@NotNull DisplayedLine line);

    void scheduleUpdateTasks();

    void cancelUpdateTasks();
}
