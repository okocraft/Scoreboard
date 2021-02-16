package net.okocraft.scoreboard.display.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Placeholders {

    private static final Object SERVER;
    private static final Field TPS_FIELD;

    private static Method PLAYER_GET_HANDLE;
    private static Field PLAYER_PING;

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
            var value = getPing(p);
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

    private static int getPing(@NotNull Player player) {
        if (PLAYER_PING == null) {
            try {
                cacheReflection(player);
            } catch (Throwable e) {
                e.printStackTrace();
                return -1;
            }
        }

        try {
            var minecraftPlayer = PLAYER_GET_HANDLE.invoke(player);
            return PLAYER_PING.getInt(minecraftPlayer);
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void cacheReflection(Player player) throws Throwable {
        PLAYER_GET_HANDLE = player.getClass().getDeclaredMethod("getHandle");
        PLAYER_GET_HANDLE.setAccessible(true);

        var minecraftPlayer = PLAYER_GET_HANDLE.invoke(player);

        PLAYER_PING = minecraftPlayer.getClass().getDeclaredField("ping");
        PLAYER_PING.setAccessible(true);
    }
}
