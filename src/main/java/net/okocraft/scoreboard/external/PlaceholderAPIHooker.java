package net.okocraft.scoreboard.external;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Server;
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

    public static boolean checkEnabled(@NotNull Server server) {
        ENABLED = server.getPluginManager().getPlugin("PlaceholderAPI") != null;
        return ENABLED;
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
