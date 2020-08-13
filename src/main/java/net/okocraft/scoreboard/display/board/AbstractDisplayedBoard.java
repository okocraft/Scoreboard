package net.okocraft.scoreboard.display.board;

import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.display.line.DisplayedLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class AbstractDisplayedBoard implements DisplayedBoard {

    protected final ScoreboardPlugin plugin;
    protected final Player player;

    public AbstractDisplayedBoard(@NotNull ScoreboardPlugin plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public void update() {
        getTitle().update();
        getLines().forEach(DisplayedLine::update);

        plugin.getExecutor().submit(this::apply);
    }

    protected abstract void apply();

    @NotNull
    protected abstract DisplayedLine getTitle();

    @NotNull
    protected abstract List<DisplayedLine> getLines();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractDisplayedBoard)) return false;
        AbstractDisplayedBoard that = (AbstractDisplayedBoard) o;
        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
