package net.okocraft.scoreboard.listener;

import net.okocraft.scoreboard.ScoreboardPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;

public class PluginListener implements Listener {

    private final ScoreboardPlugin plugin;

    public PluginListener(@NotNull ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnable(@NotNull PluginEnableEvent event) {
        switch (event.getPlugin().getName()) {
            case "PlaceholderAPI":
                plugin.checkPlaceholderAPI();
                return;
            case "ProtocolLib":
                plugin.checkProtocolLib();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisable(@NotNull PluginDisableEvent event) {
        switch (event.getPlugin().getName()) {
            case "PlaceholderAPI":
                plugin.checkPlaceholderAPI();
                return;
            case "ProtocolLib":
                plugin.checkProtocolLib();
        }
    }
}
