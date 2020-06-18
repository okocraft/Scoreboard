package net.okocraft.scoreboard.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PapiReplacer {

    @NotNull
    public static String run(@NotNull Player player, @NotNull String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    public static boolean hasPlaceholder(@NotNull String str) {
        return PlaceholderAPI.containsPlaceholders(str);
    }
}
