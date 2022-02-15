package net.okocraft.scoreboard.display.placeholder;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Placeholders {

    private static final Object SERVER;
    private static final Field TPS_FIELD;

    static {
        try {
            SERVER = Bukkit.getServer().getClass()
                    .getDeclaredMethod("getServer").invoke(Bukkit.getServer());

            TPS_FIELD = SERVER.getClass().getField("recentTps");
            TPS_FIELD.setAccessible(true);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private Placeholders() {
    }

    @NotNull
    public static String replace(@NotNull Player p, @NotNull String line) {
        if (line.contains("%server_tps%")) {
            line = line.replace("%server_tps%", Double.toString(getTps()));
        }

        if (line.contains("%player_name%")) {
            line = line.replace("%player_name%", p.getName());
        }

        if (line.contains("%player_displayname%")) {
            line = line.replace(
                    "%player_displayname%",
                    LegacyComponentSerializer.legacyAmpersand().serialize(p.displayName())
            );
        }

        if (line.contains("%player_block_x%")) {
            var value = p.getLocation().getBlockX();
            line = line.replace("%player_block_x%", Integer.toString(value));
        }

        if (line.contains("%player_block_y%")) {
            var value = p.getLocation().getBlockY();
            line = line.replace("%player_block_y%", Integer.toString(value));
        }

        if (line.contains("%player_block_z%")) {
            var value = p.getLocation().getBlockZ();
            line = line.replace("%player_block_z%", Integer.toString(value));
        }

        if (line.contains("%player_ping%")) {
            var value = p.getPing();
            line = line.replace("%player_ping%", Integer.toString(value));
        }

        return line;
    }

    private static double getTps() {
        try {
            double[] recentTps = (double[]) TPS_FIELD.get(SERVER);

            return BigDecimal.valueOf(recentTps[0])
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
