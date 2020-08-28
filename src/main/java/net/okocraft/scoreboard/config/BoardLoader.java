package net.okocraft.scoreboard.config;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.Line;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class BoardLoader {

    private static final String DEFAULT_BOARD_FILE_NAME = "default.yml";

    private static final String PATH_TITLE = "title";
    private static final String PATH_LINE = "line";
    private static final String PATH_LIST_SUFFIX = ".list";
    private static final String PATH_INTERVAL_SUFFIX = ".interval";


    private BoardLoader() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Board loadDefaultBoard(@NotNull ScoreboardPlugin plugin) throws IllegalStateException {
        Board board = load(new BukkitConfig(plugin, DEFAULT_BOARD_FILE_NAME, true));

        if (board == null) {
            throw new IllegalStateException("Could not load default board ("+ DEFAULT_BOARD_FILE_NAME +")");
        } else {
            return board;
        }
    }

    @NotNull
    @Unmodifiable
    public static Set<Board> loadCustomBoards(@NotNull ScoreboardPlugin plugin) throws IllegalStateException {
        Path dirPath = plugin.getDataFolder().toPath().resolve("boards");

        if (Files.exists(dirPath)) {
            try {
                return Files.list(dirPath)
                        .filter(Files::isRegularFile)
                        .filter(Files::isReadable)
                        .filter(p -> !p.toString().endsWith(DEFAULT_BOARD_FILE_NAME))
                        .map(BukkitYaml::new)
                        .map(BoardLoader::load)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            try {
                Files.createDirectories(dirPath);
                return Collections.emptySet();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Nullable
    private static Board load(@NotNull BukkitYaml yaml) {
        if (!yaml.load()) {
            return null;
        }

        List<String> titleList = yaml.getStringList(PATH_TITLE + PATH_LIST_SUFFIX);
        Line title;

        if (titleList.isEmpty()) {
            title = Line.EMPTY;
        } else {
            title = new Line(titleList, yaml.getLong(PATH_TITLE + PATH_INTERVAL_SUFFIX));
        }

        ConfigurationSection section = yaml.getConfig().getConfigurationSection(PATH_LINE);

        List<Line> lines;

        if (section == null) {
            lines = Collections.emptyList();
        } else {
            lines = new LinkedList<>();

            for (String root : section.getKeys(false)) {
                List<String> lineList = section.getStringList(root + PATH_LIST_SUFFIX);

                if (lineList.isEmpty()) {
                    lines.add(Line.EMPTY);
                } else {
                    lines.add(new Line(lineList, section.getLong(root + PATH_INTERVAL_SUFFIX)));
                }
            }
        }

        return new Board(title, lines);
    }
}
