package net.okocraft.scoreboard.papi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAPIHooker {

    private static boolean ENABLED;

    public static void checkEnabled() {
        ENABLED = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static String run(@NotNull Player player, @NotNull String str) {
        if (isEnabled() && str.contains("%")) {
            return PlaceholderReplacer.run(player, str);
        } else {
            return str;
        }
    }
}
