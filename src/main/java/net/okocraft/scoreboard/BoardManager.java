package net.okocraft.scoreboard;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.display.board.BukkitDisplayedBoard;
import net.okocraft.scoreboard.display.board.DisplayedBoard;
import net.okocraft.scoreboard.display.board.PacketDisplayBoard;
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

    BoardManager(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
        this.displayedBoards = new HashSet<>();

        defBoard = loadBoard(new BukkitConfig(plugin, "default.yml", true));
    }

    public void showAllDefault() {
        plugin.getServer().getOnlinePlayers().forEach(this::showDefault);
    }

    public void showDefault(@NotNull Player player) {
        DisplayedBoard display;

        if (plugin.isUsingProtocolLib()) {
            display = new PacketDisplayBoard(plugin, defBoard, player);
        } else {
            display = new BukkitDisplayedBoard(plugin, defBoard, player);
        }

        plugin.getExecutor().submit(display::scheduleUpdateTasks);
        displayedBoards.add(display);
    }

    public void removeBoard(@NotNull Player player) {
        Set<DisplayedBoard> playerBoards =
                displayedBoards.stream().filter(b -> b.getPlayer().equals(player)).collect(Collectors.toSet());

        for (DisplayedBoard board : playerBoards) {
            board.cancelUpdateTasks();
            displayedBoards.remove(board);
        }

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

    @NotNull
    private Board loadBoard(@NotNull BukkitYaml yaml) {
        if (!yaml.load()) {
            throw new IllegalStateException("Could not load " + yaml.getPath().getFileName().toString());
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
