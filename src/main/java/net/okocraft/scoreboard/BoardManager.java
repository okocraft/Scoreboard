package net.okocraft.scoreboard;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.player.BoardDisplay;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardManager {

    private final ScoreboardPlugin plugin;
    private final Board defBoard;
    private final Set<BoardDisplay> displayedBoards;
    private final long updateInterval;

    BoardManager(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
        this.displayedBoards = new HashSet<>();
        defBoard = loadBoard(new BukkitConfig(plugin, "default.yml", true));

        if (defBoard == null) {
            throw new IllegalStateException("Could not load default board");
        }

        updateInterval =
                defBoard.getLines().stream().map(Line::getInterval).filter(i -> 0 < i).sorted().findFirst().orElse(1L);

        plugin.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, this::update, updateInterval, updateInterval);
    }

    public void showAllDefault() {
        plugin.getServer().getOnlinePlayers().forEach(this::showDefault);
    }

    public void showDefault(@NotNull Player player) {
        displayedBoards.add(new BoardDisplay(plugin, defBoard, player));
    }

    public void removeBoard(@NotNull Player player) {
        Set<BoardDisplay> displayed = displayedBoards.stream().filter(b -> b.getPlayer().equals(player)).collect(Collectors.toSet());
        displayed.forEach(displayedBoards::remove);
        player.setScoreboard(plugin.getScoreboardManager().getMainScoreboard());
    }

    public void removeAll() {
        for (BoardDisplay displayed : displayedBoards) {
            Player player = displayed.getPlayer();
            if (player.isOnline()) {
                player.setScoreboard(plugin.getScoreboardManager().getMainScoreboard());
            }
        }

        displayedBoards.clear();
    }

    public void update() {
        defBoard.update(updateInterval);
        displayedBoards.forEach(BoardDisplay::update);
    }

    @Nullable
    private Board loadBoard(@NotNull BukkitYaml yaml) {
        if (!yaml.load()) {
            return null;
        }

        List<String> titleList = yaml.getStringList("title.list");

        if (titleList.isEmpty()) {
            return null;
        }

        Line title = new Line(titleList, yaml.getLong("title.interval", 5));

        ConfigurationSection section = yaml.getConfig().getConfigurationSection("line");
        if (section == null) {
            return null;
        }

        List<Line> lines = new LinkedList<>();
        for (String root : section.getKeys(false)) {
            List<String> lineList = section.getStringList(root + ".list");

            if (!lineList.isEmpty()) {
                lines.add(new Line(lineList, section.getLong(root + ".interval")));
            } else {
                lines.add(Line.EMPTY);
            }
        }

        return new Board(title, lines);
    }
}
