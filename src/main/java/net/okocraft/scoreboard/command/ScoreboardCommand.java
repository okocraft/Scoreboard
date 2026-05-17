package net.okocraft.scoreboard.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.command.subcommand.HideCommand;
import net.okocraft.scoreboard.command.subcommand.ReloadCommand;
import net.okocraft.scoreboard.command.subcommand.ShowCommand;
import net.okocraft.scoreboard.message.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScoreboardCommand extends Command {

    private static final String COMMAND_PERMISSION = "scoreboard.command";

    private final SubCommandHolder subCommandHolder;

    public ScoreboardCommand(@NotNull ScoreboardPlugin plugin) {
        super("sboard", "The command for Scoreboard plugin", "", List.of("sb"));
        this.subCommandHolder = new SubCommandHolder(
            new ShowCommand(plugin.getBoardManager(), plugin.getDisplayManager()),
            new HideCommand(plugin.getDisplayManager()),
            new ReloadCommand(plugin)
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission(COMMAND_PERMISSION))) {
            sender.sendMessage(Messages.NO_PERMISSION.apply(COMMAND_PERMISSION));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            this.sendHelp(sender);
            return true;
        }

        var optionalSubCommand = this.subCommandHolder.search(args[0]);

        if (optionalSubCommand.isEmpty()) {
            this.sendHelp(sender);
            return true;
        }

        var subCommand = optionalSubCommand.get();

        if (sender.hasPermission(subCommand.getPermissionNode())) {
            subCommand.onCommand(sender, args);
        } else {
            sender.sendMessage(Messages.NO_PERMISSION.apply(subCommand.getPermissionNode()));
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(args);

        if (args.length == 0 || !sender.hasPermission(COMMAND_PERMISSION)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return this.subCommandHolder.getSubCommands().stream()
                .filter(cmd -> sender.hasPermission(cmd.getPermissionNode()))
                .map(net.okocraft.scoreboard.command.Command::getName)
                .filter(cmdName -> cmdName.startsWith(args[0].toLowerCase(Locale.ROOT)))
                .toList();
        }

        return this.subCommandHolder.search(args[0])
            .filter(cmd -> sender.hasPermission(cmd.getPermissionNode()))
            .map(cmd -> cmd.onTabComplete(sender, args))
            .orElse(Collections.emptyList());
    }

    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Messages.COMMAND_HELP_HEADER);
        sender.sendMessage(Component.join(
            JoinConfiguration.newlines(),
            ((Iterable<Component>) this.subCommandHolder.getSubCommands().stream().map(net.okocraft.scoreboard.command.Command::getHelp)::iterator)
        ));
    }
}
