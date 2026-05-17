package net.okocraft.scoreboard.config;

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

@NullMarked
@ConfigSerializable
public class Config {

    public static Config loadFrom(Path filepath) throws ConfigurateException {
        return YamlConfigurationLoader.builder().path(filepath).build().load().get(Config.class, new Config());
    }

    public int maxLineLength = 32;

    @PostProcess
    public void postProcess() {
        this.maxLineLength = Math.max(this.maxLineLength, 1);
    }

}
