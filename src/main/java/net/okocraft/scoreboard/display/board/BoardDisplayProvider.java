package net.okocraft.scoreboard.display.board;

import net.okocraft.scoreboard.board.Board;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BoardDisplayProvider {
    @NotNull
    BoardDisplay newDisplay(@NotNull Player player, @NotNull Board board);
}
