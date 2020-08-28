package net.okocraft.scoreboard.listener;

import net.okocraft.scoreboard.ScoreboardPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    private final ScoreboardPlugin plugin;

    public PlayerListener(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        plugin.getDisplayManager().showDefaultBoard(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        plugin.runAsync(() -> plugin.getDisplayManager().hideBoard(event.getPlayer()));
    }
}
