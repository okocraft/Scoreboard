package net.okocraft.scoreboard.display.manager;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import net.okocraft.scoreboard.display.board.PacketBoardDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketDisplayManager extends AbstractDisplayManager {

    public PacketDisplayManager(@NotNull ScoreboardPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    protected BoardDisplay newDisplay(@NotNull Player player, @NotNull Board board) {
        return new PacketBoardDisplay(plugin, board, player);
    }

    @Override
    public boolean isUsingProtocolLib() {
        return true;
    }
}
