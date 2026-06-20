package net.okocraft.scoreboard;

import dev.siroshun.mcmsgdef.directory.DirectorySource;
import dev.siroshun.mcmsgdef.directory.MessageProcessors;
import dev.siroshun.mcmsgdef.file.PropertiesFile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import net.okocraft.scoreboard.board.line.LineFormat;
import net.okocraft.scoreboard.command.ScoreboardCommand;
import net.okocraft.scoreboard.config.BoardManager;
import net.okocraft.scoreboard.config.Config;
import net.okocraft.scoreboard.display.board.BoardDisplayProvider;
import net.okocraft.scoreboard.display.board.BukkitBoardDisplay;
import net.okocraft.scoreboard.display.line.LineDisplay;
import net.okocraft.scoreboard.display.manager.BoardDisplayManager;
import net.okocraft.scoreboard.display.placeholder.Placeholder;
import net.okocraft.scoreboard.display.placeholder.PlaceholderProvider;
import net.okocraft.scoreboard.external.PlaceholderAPIHooker;
import net.okocraft.scoreboard.listener.PlayerListener;
import net.okocraft.scoreboard.listener.PluginListener;
import net.okocraft.scoreboard.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ScoreboardPlugin extends JavaPlugin {

    private final PlaceholderProvider placeholderProvider;
    private final LineFormat.Compiler lineCompiler;
    private final BoardDisplayProvider displayProvider;
    private final BoardManager boardManager = new BoardManager(this);

    private BoardDisplayManager displayManager;
    private PlayerListener playerListener;
    private PluginListener pluginListener;

    public ScoreboardPlugin() {
        this.placeholderProvider = new PlaceholderProvider(PlaceholderAPIHooker::createPlaceholder);
        this.displayProvider = BukkitBoardDisplay.createProvider(this);
        this.lineCompiler = LineFormat.compiler(this.placeholderProvider);
        Placeholder.registerDefaults(this.placeholderProvider);
    }

    public ScoreboardPlugin(@NotNull BoardDisplayProvider displayProvider) {
        this.placeholderProvider = new PlaceholderProvider(PlaceholderAPIHooker::createPlaceholder);
        this.displayProvider = displayProvider;
        this.lineCompiler = LineFormat.compiler(this.placeholderProvider);
    }

    @Override
    public void onLoad() {
        this.reloadSettings(ex -> {
        });
    }

    @Override
    public void onEnable() {
        this.playerListener = new PlayerListener(this);
        this.playerListener.register();

        this.pluginListener = new PluginListener(this);
        this.pluginListener.register();

        this.displayManager = new BoardDisplayManager(this.boardManager, this.displayProvider);

        if (PlaceholderAPIHooker.checkEnabled(this.getServer())) {
            this.printPlaceholderIsAvailable();
        }

        Bukkit.getCommandMap().register("sboard", new ScoreboardCommand(this));
    }

    @Override
    public void onDisable() {
        if (this.playerListener != null) {
            this.playerListener.unregister();
        }

        if (this.displayManager != null) {
            this.displayManager.hideAllBoards();
        }

        if (this.pluginListener != null) {
            this.pluginListener.unregister();
        }

        this.getServer().getAsyncScheduler().cancelTasks(this);
    }

    public boolean reloadSettings(@NotNull Consumer<Throwable> exceptionConsumer) {
        try {
            Config config = Config.loadFrom(this.saveResource("config.yml"));
            LineDisplay.globalLengthLimit = Math.max(config.maxLineLength, 1);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Could not load config.yml", e);
            exceptionConsumer.accept(e);
            return false;
        }

        try {
            this.loadMessages();
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Could not load languages", e);
            exceptionConsumer.accept(e);
            return false;
        }

        try {
            this.boardManager.reload();
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Could not load boards", e);
            exceptionConsumer.accept(e);
            return false;
        }

        return true;
    }

    public @NotNull PlaceholderProvider getPlaceholderProvider() {
        return this.placeholderProvider;
    }

    public @NotNull LineFormat.Compiler getLineCompiler() {
        return this.lineCompiler;
    }

    @NotNull
    public BoardManager getBoardManager() {
        return this.boardManager;
    }

    public BoardDisplayManager getDisplayManager() {
        if (this.displayManager == null) {
            throw new IllegalStateException();
        }

        return this.displayManager;
    }

    public void printPlaceholderIsAvailable() {
        this.getLogger().info("PlaceholderAPI is available!");
    }

    public Path saveResource(String filename) throws IOException {
        var filepath = this.getDataFolder().toPath().resolve(filename);
        if (!Files.isRegularFile(filepath)) {
            Files.createDirectories(this.getDataFolder().toPath());
            try (var input = this.getResource(filename)) {
                if (input == null) {
                    throw new IllegalStateException(filename + " was not found in the jar.");
                }
                Files.copy(input, filepath);
            }
        }
        return filepath;
    }

    private void loadMessages() throws IOException {
        Key languageKey = Key.key("scoreboard", "language");
        for (Translator source : GlobalTranslator.translator().sources()) {
            if (source.name().equals(languageKey)) {
                GlobalTranslator.translator().removeSource(source);
            }
        }

        DirectorySource.propertiesFiles(this.getDataFolder().toPath().resolve("languages"))
            .defaultLocale(Locale.ENGLISH, Locale.JAPANESE)
            .primaryLocale(Locale.ENGLISH)
            .messageProcessor(MessageProcessors.appendMissingMessagesToPropertiesFile(this::loadDefaultMessageMap))
            .loadAndRegister(languageKey);
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
}
