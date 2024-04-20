package net.okocraft.scoreboard;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.messages.api.directory.DirectorySource;
import com.github.siroshun09.messages.api.directory.MessageProcessors;
import com.github.siroshun09.messages.api.source.StringMessageMap;
import com.github.siroshun09.messages.api.util.PropertiesFile;
import com.github.siroshun09.messages.minimessage.localization.MiniMessageLocalization;
import com.github.siroshun09.messages.minimessage.source.MiniMessageSource;
import net.okocraft.scoreboard.command.ScoreboardCommand;
import net.okocraft.scoreboard.config.BoardManager;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.display.manager.BukkitDisplayManager;
import net.okocraft.scoreboard.display.manager.DisplayManager;
import net.okocraft.scoreboard.external.PlaceholderAPIHooker;
import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.listener.PluginListener;
import net.okocraft.scoreboard.message.Messages;
import net.okocraft.scoreboard.util.scheduler.Scheduler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ScoreboardPlugin extends JavaPlugin {

    private static ScoreboardPlugin INSTANCE;

    public static @NotNull Plugin getPlugin() {
        return Objects.requireNonNull(INSTANCE);
    }

    private final Scheduler scheduler = Scheduler.create();

    private MiniMessageLocalization localization;
    private BoardManager boardManager;
    private DisplayManager displayManager;
    private PlayerListener playerListener;
    private PluginListener pluginListener;

    @Override
    public void onLoad() {
        INSTANCE = this;

        try {
            saveDefaultFiles();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save default files", e);
        }

        try {
            this.loadMessages();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load languages", e);
        }

        loadConfig();
        boardManager = new BoardManager(this);
        boardManager.reload();
    }

    @Override
    public void onEnable() {
        playerListener = new PlayerListener(this);
        playerListener.register();

        pluginListener = new PluginListener(this);
        pluginListener.register();

        displayManager = new BukkitDisplayManager(this);

        if (PlaceholderAPIHooker.checkEnabled(getServer())) {
            printPlaceholderIsAvailable();
        }

        var command = getCommand("sboard");

        if (command != null) {
            var impl = new ScoreboardCommand(this);
            command.setExecutor(impl);
            command.setTabCompleter(impl);
        }

        scheduler.runAsync(this::showDefaultBoardToOnlinePlayers);
    }

    @Override
    public void onDisable() {
        if (playerListener != null) {
            playerListener.unregister();
        }

        if (displayManager != null) {
            displayManager.hideAllBoards();
        }

        if (pluginListener != null) {
            pluginListener.unregister();
        }

        scheduler.shutdown();
    }

    public boolean reload(@NotNull Consumer<Throwable> exceptionConsumer) {
        displayManager.hideAllBoards();

        try {
            this.loadMessages();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load languages", e);
            exceptionConsumer.accept(e);
            return false;
        }

        // TODO: exception handling
        loadConfig();
        boardManager.reload();

        scheduler.runAsync(this::showDefaultBoardToOnlinePlayers);
        return true;
    }

    public @NotNull Scheduler getScheduler() {
        return scheduler;
    }

    public @NotNull MiniMessageLocalization getLocalization() {
        return this.localization;
    }

    @NotNull
    public BoardManager getBoardManager() {
        if (boardManager == null) {
            throw new IllegalStateException();
        }

        return boardManager;
    }

    public DisplayManager getDisplayManager() {
        if (boardManager == null) {
            throw new IllegalStateException();
        }

        return displayManager;
    }

    public void printPlaceholderIsAvailable() {
        getLogger().info("PlaceholderAPI is available!");
    }

    private void saveDefaultFiles() throws IOException {
        ResourceUtils.copyFromJarIfNotExists(
                getFile().toPath(), "config.yml", getDataFolder().toPath().resolve("config.yml")
        );

        ResourceUtils.copyFromJarIfNotExists(
                getFile().toPath(), "default.yml", getDataFolder().toPath().resolve("default.yml")
        );
    }

    private void loadConfig() {
        try (var config = YamlConfiguration.create(getDataFolder().toPath().resolve("config.yml"))) {
            config.load();
            LineDisplay.globalLengthLimit = Math.max(config.getInteger("max-line-length", 32), 1);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load config.yml", e);
        }
    }

    private void loadMessages() throws IOException {
        if (this.localization == null) { // on startup
            this.localization = new MiniMessageLocalization(MiniMessageSource.create(StringMessageMap.create(Messages.defaultMessages())), Messages::getLocaleFrom);
        } else { // on reload
            this.localization.clearSources();
        }

        DirectorySource.forStringMessageMap(this.getDataFolder().toPath().resolve("languages"))
                .fileExtension(PropertiesFile.FILE_EXTENSION)
                .defaultLocale(Locale.ENGLISH, Locale.JAPANESE)
                .messageLoader(PropertiesFile.DEFAULT_LOADER)
                .messageProcessor(MessageProcessors.appendMissingStringMessages(this::loadDefaultMessageMap, PropertiesFile.DEFAULT_APPENDER))
                .messageProcessor(loaded -> MiniMessageSource.create(loaded.messageSource()))
                .load(loaded -> this.localization.addSource(loaded.locale(), loaded.messageSource()));
    }

    private @Nullable Map<String, String> loadDefaultMessageMap(@NotNull Locale locale) throws IOException {
        if (locale.equals(Locale.ENGLISH)) {
            return Messages.defaultMessages();
        } else {
            try (var input = this.getResource(locale + ".properties")) {
                return input != null ? PropertiesFile.load(input) : null;
            }
        }
    }

    private void showDefaultBoardToOnlinePlayers() {
        getServer().getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("scoreboard.show-on-join"))
                .forEach(displayManager::showDefaultBoard);
    }
}
