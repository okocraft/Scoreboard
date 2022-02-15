package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.Line;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

final class BoardLoader {

    private static final String DEFAULT_BOARD_FILE_NAME = "default.yml";

    private static final String PATH_TITLE = "title";
    private static final String PATH_LINE = "line";
    private static final String PATH_LIST_SUFFIX = ".list";
    private static final String PATH_INTERVAL_SUFFIX = ".interval";

    private BoardLoader() {
        throw new UnsupportedOperationException();
    }

    static @NotNull Board loadDefaultBoard(@NotNull ScoreboardPlugin plugin) {
        var yaml = YamlConfiguration.create(plugin.getDataFolder().toPath().resolve("default.yml"));

        try {
            yaml.load();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load default.yml", e);
        }

        return getBoard(yaml);
    }

    static @NotNull @Unmodifiable List<Board> loadCustomBoards(@NotNull ScoreboardPlugin plugin) {
        Path dirPath = plugin.getDataFolder().toPath().resolve("boards");

        if (Files.exists(dirPath)) {
            try {
                return Files.list(dirPath)
                        .filter(Files::isRegularFile)
                        .filter(Files::isReadable)
                        .filter(p -> !p.toString().endsWith(DEFAULT_BOARD_FILE_NAME))
                        .map(YamlConfiguration::create)
                        .map(yaml -> {
                            try {
                                yaml.load();
                                return yaml;
                            } catch (IOException e) {
                                plugin.getLogger().log(Level.SEVERE, "Could not load " + yaml.getPath().getFileName(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .map(BoardLoader::getBoard)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not load board files", e);
                return Collections.emptyList();
            }
        } else {
            try {
                Files.createDirectories(dirPath);
                return Collections.emptyList();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create board directory", e);
                return Collections.emptyList();
            }
        }
    }

    private static @NotNull Board getBoard(@NotNull YamlConfiguration yaml) {
        List<String> titleList = yaml.getStringList(PATH_TITLE + PATH_LIST_SUFFIX);
        Line title;

        if (titleList.isEmpty()) {
            title = Line.EMPTY;
        } else {
            title = new Line(titleList, yaml.getLong(PATH_TITLE + PATH_INTERVAL_SUFFIX));
        }

        var section = yaml.getSection(PATH_LINE);

        List<Line> lines;

        if (section == null) {
            lines = Collections.emptyList();
        } else {
            lines = new LinkedList<>();

            for (String root : section.getKeyList()) {
                List<String> lineList = section.getStringList(root + PATH_LIST_SUFFIX);

                if (lineList.isEmpty()) {
                    lines.add(Line.EMPTY);
                } else {
                    lines.add(new Line(lineList, section.getLong(root + PATH_INTERVAL_SUFFIX)));
                }
            }
        }

        var name = yaml.getPath().getFileName().toString();

        return new Board(name.substring(0, name.lastIndexOf('.')), title, lines);
    }
}
