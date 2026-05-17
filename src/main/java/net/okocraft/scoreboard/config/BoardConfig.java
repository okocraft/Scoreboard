package net.okocraft.scoreboard.config;

import org.jspecify.annotations.NullUnmarked;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullUnmarked
@ConfigSerializable
public class BoardConfig {

    public static BoardConfig loadFrom(Path filepath) throws ConfigurateException {
        return YamlConfigurationLoader.builder().path(filepath).build().load().get(BoardConfig.class);
    }

    public transient String name;
    public LineSection title;
    public Map<String, LineSection> lines;

    @PostProcess
    public void postProcess() {
        this.title = Objects.requireNonNullElse(this.title, new LineSection());
        this.lines = Objects.requireNonNullElse(this.lines, Map.of());
    }

    @ConfigSerializable
    public static class LineSection {
        public int interval = 0;
        public int lengthLimit = -1;
        public List<String> list;

        @PostProcess
        public void postProcess() {
            this.interval = Math.max(this.interval, 0);
            this.lengthLimit = Math.max(this.lengthLimit, -1);
            this.list = Objects.requireNonNullElse(this.list, List.of());
            this.list = this.list.stream().map(str -> Objects.requireNonNullElse(str, "")).toList();
        }
    }
}
