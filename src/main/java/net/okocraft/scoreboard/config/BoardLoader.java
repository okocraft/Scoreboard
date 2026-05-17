package net.okocraft.scoreboard.config;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Board;
import net.okocraft.scoreboard.board.line.Line;
import net.okocraft.scoreboard.board.line.LineFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

final class BoardLoader {

    private BoardLoader() {
        throw new UnsupportedOperationException();
    }

    static @NotNull Board loadDefaultBoard(@NotNull ScoreboardPlugin plugin) throws IOException {
        BoardConfig config = loadBoardConfig(plugin.saveResource("default.yml"));
        return createBoardFromConfig(config, plugin.getLineCompiler());
    }

    static @NotNull @Unmodifiable List<Board> loadCustomBoards(@NotNull ScoreboardPlugin plugin) {
        Path dirPath = plugin.getDataFolder().toPath().resolve("boards");

        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create board directory", e);
            }

            return Collections.emptyList();
        }

        try (var listStream = Files.list(dirPath)) {
            return listStream
                .filter(Files::isRegularFile)
                .filter(Files::isReadable)
                .filter(p -> checkFilename(p.getFileName().toString()))
                .map(filepath -> {
                    try {
                        return loadBoardConfig(filepath);
                    } catch (IOException e) {
                        plugin.getLogger().log(Level.SEVERE, "Could not load " + filepath.getFileName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(config -> createBoardFromConfig(config, plugin.getLineCompiler()))
                .collect(Collectors.toList());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load board files", e);
            return Collections.emptyList();
        }
    }

    private static @NotNull BoardConfig loadBoardConfig(@NotNull Path filepath) throws IOException {
        BoardConfig config = BoardConfig.loadFrom(filepath);
        config.name = getBoardName(filepath);
        return config;
    }

    private static boolean checkFilename(String filename) {
        var boardName = filename.substring(0, filename.lastIndexOf('.'));
        return isYaml(filename) && !boardName.equals("default");
    }

    private static boolean isYaml(String filename) {
        var checking = filename.toLowerCase(Locale.ENGLISH);
        return (checking.endsWith(".yml") && 4 < checking.length()) || (checking.endsWith(".yaml") && 5 < checking.length());
    }

    private static String getBoardName(Path filepath) {
        var name = filepath.getFileName().toString();
        return name.substring(0, name.lastIndexOf('.'));
    }

    private static @NotNull Board createBoardFromConfig(@NotNull BoardConfig config, @NotNull LineFormat.Compiler compiler) {
        return new Board(
            config.name,
            createLineFromSection(config.title, compiler),
            config.lines.values().stream().map(section -> createLineFromSection(section, compiler)).toList()
        );
    }

    private static @NotNull Line createLineFromSection(@NotNull BoardConfig.LineSection section, @NotNull LineFormat.Compiler compiler) {
        if (section.list.isEmpty()) {
            return Line.EMPTY;
        } else {
            return new Line(
                section.list.stream().map(LegacyComponentSerializer.legacyAmpersand()::deserialize).map(compiler::compile).toList(),
                section.interval,
                section.lengthLimit
            );
        }
    }
}
