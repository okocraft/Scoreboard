package net.okocraft.scoreboard.command.subcommand;

import net.kyori.adventure.text.Component;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.command.AbstractCommand;
import net.okocraft.scoreboard.message.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends AbstractCommand {

    private final ScoreboardPlugin plugin;

    public ReloadCommand(@NotNull ScoreboardPlugin plugin) {
        super("reload", "scoreboard.command.reload");
        this.plugin = plugin;
    }

    @Override
    public @NotNull Component getHelp() {
        return Messages.RELOAD_HELP;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        this.plugin.getDisplayManager().hideAllBoards();

        if (this.plugin.reloadSettings(ex -> sender.sendMessage(Messages.RELOAD_ERROR.apply(ex)))) {
            this.plugin.getServer().getAsyncScheduler().runNow(
                this.plugin,
                ignored ->
                    this.plugin.getServer().getOnlinePlayers()
                        .stream()
                        .filter(player -> player.hasPermission("scoreboard.show-on-join"))
                        .forEach(this.plugin.getDisplayManager()::showDefaultBoard)
            );

            sender.sendMessage(Messages.RELOAD_FINISH);
        }
    }
}
