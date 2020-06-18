package net.okocraft.scoreboard.papi;

import net.okocraft.scoreboard.util.PapiReplacer;
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
        if (isEnabled() && PapiReplacer.hasPlaceholder(str)) {
            return PapiReplacer.run(player, str);
        } else {
            return str;
        }
    }
}
