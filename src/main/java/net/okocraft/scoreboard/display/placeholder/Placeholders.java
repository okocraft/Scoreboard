package net.okocraft.scoreboard.display.placeholder;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Placeholders {

    public static @NotNull String replace(@NotNull Player player, @NotNull String line) {
        var locationSnapshot = player.getLocation();
        var resultBuilder = new StringBuilder();

        boolean inPlaceholder = false;

        var placeholderBuilder = new StringBuilder();

        for (int codePoint : line.codePoints().toArray()) {
            if (codePoint == '%') {
                placeholderBuilder.appendCodePoint(codePoint);

                if (inPlaceholder) {
                    resultBuilder.append(processPlaceholder(player, locationSnapshot, placeholderBuilder.toString()));
                    placeholderBuilder.setLength(0);
                }

                inPlaceholder = !inPlaceholder;
            } else {
                if (inPlaceholder) {
                    placeholderBuilder.appendCodePoint(codePoint);
                } else {
                    resultBuilder.appendCodePoint(codePoint);
                }
            }
        }

        return resultBuilder.toString();
    }

    private static @NotNull String processPlaceholder(@NotNull Player player, @NotNull Location locationSnapshot, @NotNull String placeholder) {
        //@formatter:off
        return switch (placeholder) {
            case "%server_tps%" -> BigDecimal.valueOf(Bukkit.getTPS()[0]).setScale(2, RoundingMode.HALF_UP).toPlainString();
            case "%server_online%" -> Integer.toString(Bukkit.getOnlinePlayers().size());
            case "%player_name%" -> player.getName();
            case "%player_displayname%" -> LegacyComponentSerializer.legacyAmpersand().serialize(player.displayName());
            case "%player_world%" -> player.getWorld().getName();
            case "%player_block_x%" -> Integer.toString(locationSnapshot.getBlockX());
            case "%player_block_y%" -> Integer.toString(locationSnapshot.getBlockY());
            case "%player_block_z%" -> Integer.toString(locationSnapshot.getBlockZ());
            case "%player_ping%" -> Integer.toString(player.getPing());
            default -> placeholder;
        };
        //@formatter:on
    }

    private Placeholders() {
    }
}
