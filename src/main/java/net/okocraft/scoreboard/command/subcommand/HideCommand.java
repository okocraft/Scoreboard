package net.okocraft.scoreboard.command.subcommand;

import net.kyori.adventure.text.Component;
import net.okocraft.scoreboard.command.AbstractCommand;
import net.okocraft.scoreboard.display.manager.DisplayManager;
import net.okocraft.scoreboard.message.CommandMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class HideCommand extends AbstractCommand {

    private static final String HIDE_PERMISSION = "scoreboard.command.hide";
    private static final String HIDE_PERMISSION_OTHER = HIDE_PERMISSION + ".other";
    private final DisplayManager displayManager;

    public HideCommand(@NotNull DisplayManager displayManager) {
        super("hide", HIDE_PERMISSION, Set.of("h"));
        this.displayManager = displayManager;
    }

    @Override
    public @NotNull Component getHelp() {
        return CommandMessage.HIDE_HELP;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        Player target;

        if (1 < args.length) {
            if (!sender.hasPermission(HIDE_PERMISSION_OTHER)) {
                sender.sendMessage(CommandMessage.NO_PERMISSION.apply(HIDE_PERMISSION_OTHER));
                return;
            }

            target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(CommandMessage.PLAYER_NOT_FOUND.apply(args[1]));
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

        if (displayManager.isDisplayed(target)) {
            displayManager.hideBoard(target);
        } else {
            sender.sendMessage(CommandMessage.HIDE_ALREADY);
            return;
        }

        if (sender.equals(target)) {
            sender.sendMessage(CommandMessage.HIDE_SELF);
        } else {
            sender.sendMessage(CommandMessage.HIDE_OTHER.apply(target));
        }
    }
}
