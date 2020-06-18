package net.okocraft.scoreboard.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public final class Colorizer {

    @NotNull
    public static String colorize(@NotNull String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
