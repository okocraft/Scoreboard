package net.okocraft.scoreboard.display.placeholder;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static net.kyori.adventure.text.Component.text;

public interface Placeholder {

    static @NotNull Placeholder string(@NotNull String value) {
        return new ConstantPlaceholder(Component.text(value));
    }

    static void registerDefaults(@NotNull PlaceholderProvider target) {
        target.register("server_tps", context -> text(BigDecimal.valueOf(Bukkit.getTPS()[0]).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        target.register("server_online", context -> text(Bukkit.getOnlinePlayers().size()));
        target.register("server_ram_used", context -> text(toMB(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())));
        target.register("server_ram_free", context -> text(toMB(Runtime.getRuntime().freeMemory())));
        target.register("server_ram_total", context -> text(toMB(Runtime.getRuntime().totalMemory())));
        target.register("server_ram_max", context -> text(toMB(Runtime.getRuntime().maxMemory())));
        target.register("player_name", context -> context.viewer().name());
        target.register("player_displayname", context -> context.viewer().displayName());
        target.register("player_world", context -> text(context.viewer().getWorld().getName()));
        target.register("player_block_x", context -> text(NumberConversions.floor(context.viewer().getX())));
        target.register("player_block_y", context -> text(NumberConversions.floor(context.viewer().getY())));
        target.register("player_block_z", context -> text(NumberConversions.floor(context.viewer().getZ())));
        target.register("player_ping", context -> text(context.viewer().getPing()));
    }

    private static @NotNull String toMB(long bytes) {
        return Long.toString(bytes >> 20); // bytes / 1024 / 1024 (MB)
    }

    @NotNull Component apply(@NotNull Context context);

    record Context(@NotNull Player viewer) {
    }
}
