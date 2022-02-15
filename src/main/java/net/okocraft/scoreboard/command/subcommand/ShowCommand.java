package net.okocraft.scoreboard.command.subcommand;

import net.kyori.adventure.text.Component;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.command.AbstractCommand;
import net.okocraft.scoreboard.config.BoardManager;
import net.okocraft.scoreboard.display.manager.DisplayManager;
import net.okocraft.scoreboard.message.CommandMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ShowCommand extends AbstractCommand {
    private static final String SHOW_PERMISSION = "scoreboard.command.show";
    private static final String SHOW_PERMISSION_OTHER = SHOW_PERMISSION + ".other";

    private final BoardManager boardManager;
    private final DisplayManager displayManager;

    public ShowCommand(@NotNull BoardManager boardManager, @NotNull DisplayManager displayManager) {
        super("show", SHOW_PERMISSION, Set.of("s"));
        this.boardManager = boardManager;
        this.displayManager = displayManager;
    }

    @Override
    public @NotNull Component getHelp() {
        return CommandMessage.SHOW_HELP;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        Board board;

        if (1 < args.length) {
            board = searchForBoard(args[1]);

            if (board == null) {
                sender.sendMessage(CommandMessage.BOARD_NOT_FOUND.apply(args[1]));
                return;
            }
        } else {
            board = boardManager.getDefaultBoard();
        }

        Player target;

        if (2 < args.length) {
            if (!sender.hasPermission(SHOW_PERMISSION_OTHER)) {
                sender.sendMessage(CommandMessage.NO_PERMISSION.apply(SHOW_PERMISSION_OTHER));
                return;
            }

            target = Bukkit.getPlayer(args[2]);

            if (target == null) {
                sender.sendMessage(CommandMessage.PLAYER_NOT_FOUND.apply(args[2]));
                return;
            }
        } else {
            if (sender instanceof Player player) {
                target = player;
            } else {
                sender.sendMessage(CommandMessage.ONLY_PLAYER);
                return;
            }
        }

        displayManager.showBoard(target, board);

        if (sender.equals(target)) {
            sender.sendMessage(CommandMessage.SHOW_BOARD_SELF.apply(board));
        } else {
            sender.sendMessage(CommandMessage.SHOW_BOARD_OTHER.apply(board, target));
        }
    }

    private @Nullable Board searchForBoard(@NotNull String name) {
        if (name.equalsIgnoreCase("default")) {
            return boardManager.getDefaultBoard();
        } else {
            return boardManager.getCustomBoards().stream()
                    .filter(b -> b.getName().equals(name))
                    .findFirst().orElse(null);
        }
    }
}
