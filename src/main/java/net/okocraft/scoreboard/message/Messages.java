package net.okocraft.scoreboard.message;

import dev.siroshun.mcmsgdef.DefaultMessageDefiner;
import dev.siroshun.mcmsgdef.MessageKey;
import dev.siroshun.mcmsgdef.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.okocraft.scoreboard.board.Board;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public final class Messages {

    private static final DefaultMessageDefiner DEFINER = DefaultMessageDefiner.create();

    private static final Placeholder<String> PERMISSION = node -> Argument.string("permission", node);
    private static final Placeholder<String> NAME = name -> Argument.string("name", name);
    private static final Placeholder<Board> BOARD = board -> Argument.string("board", board.name());
    private static final Placeholder<Player> PLAYER = player -> Argument.component("player", player.name().hoverEvent(player));
    private static final Placeholder<Throwable> ERROR = ex -> Argument.string("error", ex.getMessage());
    private static final Placeholder<MessageKey> COMMANDLINE_PLACEHOLDER = key -> Argument.component("commandline", key.asComponent());
    private static final Placeholder<MessageKey> HELP_PLACEHOLDER = key -> Argument.component("help", key.asComponent());

    public static final MessageKey.Arg1<String> NO_PERMISSION = DEFINER.define("scoreboard.error.no-permission", "<red>You don't have the permission: <aqua><permission>").with(PERMISSION);
    public static final MessageKey.Arg1<String> BOARD_NOT_FOUND = DEFINER.define("scoreboard.error.board-not-found", "<red>Board <aqua><name></aqua> was not found.").with(NAME);
    public static final MessageKey.Arg1<String> PLAYER_NOT_FOUND = DEFINER.define("scoreboard.error.player-not-found", "<red>Player <aqua><name></aqua> was not found.").with(NAME);
    public static final MessageKey ONLY_PLAYER = DEFINER.define("scoreboard.error.only-player", "<red>This command can only be executed by the player.");

    public static final MessageKey COMMAND_HELP_HEADER = DEFINER.define("scoreboard.help.header", "<dark_gray><st>=========================<reset><gold><b> Scoreboard <reset><dark_gray><st>=========================");
    private static final MessageKey.Arg2<MessageKey, MessageKey> COMMAND_HELP_LINE_KEY = DEFINER.define("scoreboard.help.line", "<aqua><commandline><dark_gray>: <gray><help>").with(COMMANDLINE_PLACEHOLDER, HELP_PLACEHOLDER);

    public static final Component SHOW_HELP = help(DEFINER.define("scoreboard.show.commandline", "/sb show <board> {player}"), DEFINER.define("scoreboard.show.help", "Shows the board"));
    public static final MessageKey.Arg1<Board> SHOW_SELF = DEFINER.define("scoreboard.show.self", "<gray>Board <aqua><board></aqua> is now displayed.").with(BOARD);
    public static final MessageKey.Arg2<Board, Player> SHOW_OTHER = DEFINER.define("scoreboard.show.other", "<gray>Board <aqua><board></aqua> is now displayed for player <aqua><player><aqua>.").with(BOARD, PLAYER);

    public static final Component HIDE_HELP = help(DEFINER.define("scoreboard.hide.commandline", "/sb hide {player}"), DEFINER.define("scoreboard.hide.help", "Hides the board"));
    public static final MessageKey HIDE_ALREADY = DEFINER.define("scoreboard.hide.already", "<red>The board is already hidden.");
    public static final MessageKey HIDE_SELF = DEFINER.define("scoreboard.hide.self", "<gray>The board is now hidden.");
    public static final MessageKey.Arg1<Player> HIDE_OTHER = DEFINER.define("scoreboard.hide.other", "<gray>Player <aqua><player><gray>'s board is now hidden.").with(PLAYER);

    public static final Component RELOAD_HELP = help(DEFINER.define("scoreboard.reload.commandline", "/sb reload"), DEFINER.define("scoreboard.reload.help", "Reloads configurations."));
    public static final MessageKey.Arg1<Throwable> RELOAD_ERROR = DEFINER.define("scoreboard.reload.error", "<red>An error occurred while reloading configurations: <white><error>").with(ERROR);
    public static final MessageKey RELOAD_FINISH = DEFINER.define("scoreboard.reload.finish", "<gray>Configurations have been reloaded!");

    private static @NotNull Component help(MessageKey commandlineKey, MessageKey helpKey) {
        return COMMAND_HELP_LINE_KEY.apply(commandlineKey, helpKey);
    }

    @Contract(pure = true)
    public static @NotNull @UnmodifiableView Map<String, String> defaultMessages() {
        return DEFINER.getCollectedMessages();
    }

    private Messages() {
        throw new UnsupportedOperationException();
    }
}
