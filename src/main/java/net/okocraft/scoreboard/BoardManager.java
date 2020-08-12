package net.okocraft.scoreboard;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.player.DisplayedBoard;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardManager {

    private final ScoreboardPlugin plugin;
    private final Board defBoard;
    private final Set<DisplayedBoard> displayedBoards;
    private final long interval;

    BoardManager(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
        this.displayedBoards = new HashSet<>();

        defBoard = loadBoard(new BukkitConfig(plugin, "default.yml", true));

        long minInterval = defBoard.getLines().stream()
                .map(Line::getInterval)
                .filter(i -> 0 < i).sorted()
                .findFirst()
                .orElse(1L);

        interval = Math.min(minInterval, defBoard.getTitle().getInterval());

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::update, interval, interval);
    }

    public void showAllDefault() {
        plugin.getServer().getOnlinePlayers().forEach(this::showDefault);
    }

    public void showDefault(@NotNull Player player) {
        displayedBoards.add(new DisplayedBoard(plugin, defBoard, player));
    }

    public void removeBoard(@NotNull Player player) {
        displayedBoards.stream()
                .filter(b -> b.getPlayer().equals(player))
                .collect(Collectors.toSet())
                .forEach(displayedBoards::remove);

        player.setScoreboard(plugin.getScoreboardManager().getMainScoreboard());
    }

    public void removeAll() {
        for (DisplayedBoard displayed : displayedBoards) {
            Player player = displayed.getPlayer();
            if (player.isOnline()) {
                player.setScoreboard(plugin.getScoreboardManager().getMainScoreboard());
            }
        }

        displayedBoards.clear();
    }

    public void update() {
        if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
            defBoard.update(interval);
            displayedBoards.forEach(DisplayedBoard::update);
        }
    }

    @NotNull
    private Board loadBoard(@NotNull BukkitYaml yaml) {
        if (!yaml.load()) {
            throw new IllegalStateException("Could not load default board");
        }

        List<String> titleList = yaml.getStringList("title.list");

        if (titleList.isEmpty()) {
            titleList = List.of("");
        }

        Line title = new Line(titleList, yaml.getLong("title.interval", 0));

        ConfigurationSection section = yaml.getConfig().getConfigurationSection("line");

        List<Line> lines;

        if (section == null) {
            lines = Collections.emptyList();
        } else {
            lines = new LinkedList<>();

            for (String root : section.getKeys(false)) {
                List<String> lineList = section.getStringList(root + ".list");

                if (lineList.isEmpty()) {
                    lines.add(Line.EMPTY);
                } else {
                    lines.add(new Line(lineList, section.getLong(root + ".interval")));
                }
            }
        }

        return new Board(title, lines);
    }
}
