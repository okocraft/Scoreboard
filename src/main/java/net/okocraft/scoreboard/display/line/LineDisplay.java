package net.okocraft.scoreboard.display.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.okocraft.scoreboard.ScoreboardPlugin;
import net.okocraft.scoreboard.board.Line;
import net.okocraft.scoreboard.display.placeholder.Placeholders;
import net.okocraft.scoreboard.external.PlaceholderAPIHooker;
import net.okocraft.scoreboard.util.LengthChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class LineDisplay {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    private final Player player;
    private final Line line;

    private final String name;

    private TextComponent prevLine;
    private TextComponent currentLine;

    private int currentIndex = 0;

    public LineDisplay(@NotNull Player player, @NotNull Line line, int num) {
        this.player = player;
        this.line = line;
        this.name = String.valueOf(num);

        if (line.isEmpty()) {
            this.currentLine = Component.empty();
        } else {
            var temp = PlaceholderAPIHooker.run(player, line.get(0));
            temp = LengthChecker.check(temp);
            currentLine = LegacyComponentSerializer.legacyAmpersand().deserialize(temp);
        }
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull TextComponent getCurrentLine() {
        return currentLine;
    }

    public void update() {
        currentIndex++;

        if (line.getMaxIndex() < currentIndex) {
            currentIndex = 0;
        }

        var nextLine = line.get(currentIndex);

        if (hasPlaceholders(nextLine)) {
            nextLine = Placeholders.replace(player, nextLine);
        }

        if (PlaceholderAPIHooker.isEnabled() && hasPlaceholders(nextLine)) {
            var toReplace = nextLine;
            nextLine =
                    CompletableFuture.supplyAsync(
                            () -> PlaceholderAPIHooker.run(player, toReplace),
                            Bukkit.getScheduler().getMainThreadExecutor(ScoreboardPlugin.getPlugin())
                    ).join();
        }

        nextLine = LengthChecker.check(nextLine);

        currentLine = LegacyComponentSerializer.legacyAmpersand().deserialize(nextLine);
    }

    public boolean isChanged() {
        if (currentLine.equals(prevLine)) {
            return false;
        } else {
            prevLine = currentLine;
            return true;
        }
    }

    public boolean shouldUpdate() {
        return line.shouldUpdate();
    }

    public long getInterval() {
        return line.getInterval();
    }

    private boolean hasPlaceholders(@NotNull String str) {
        return str.indexOf('%') != -1 && PLACEHOLDER_PATTERN.matcher(str).find();
    }
}
