package net.okocraft.scoreboard.display.manager;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.display.board.BoardDisplay;
import net.okocraft.scoreboard.display.board.BukkitBoardDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BukkitDisplayManager extends AbstractDisplayManager {

    public BukkitDisplayManager(@NotNull ScoreboardPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull BoardDisplay newDisplay(@NotNull Player player, @NotNull Board board) {
        var future =
                CompletableFuture.supplyAsync(
                        this::newScoreboard,
                        plugin.getServer().getScheduler().getMainThreadExecutor(plugin)
                );

        return new BukkitBoardDisplay(plugin, board, player, future.join());
    }

    private @NotNull Scoreboard newScoreboard() {
        return plugin.getServer().getScoreboardManager().getNewScoreboard();
    }
}
