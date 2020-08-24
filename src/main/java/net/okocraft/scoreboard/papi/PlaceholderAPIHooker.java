package net.okocraft.scoreboard.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAPIHooker {

    private static boolean ENABLED;

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static void setEnabled(boolean enabled) {
        ENABLED = enabled;
    }

    @NotNull
    public static String run(@NotNull Player player, @NotNull String str) {
        if (ENABLED) {
            return PlaceholderAPI.setPlaceholders(player, str);
        } else {
            return str;
        }
    }
}
