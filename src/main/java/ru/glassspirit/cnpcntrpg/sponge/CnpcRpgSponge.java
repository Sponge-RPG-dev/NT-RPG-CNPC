package ru.glassspirit.cnpcntrpg.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import ru.glassspirit.cnpcntrpg.Configuration;
import ru.glassspirit.cnpcntrpg.forge.CustomNPCsEventListener;

@Plugin(
        id = "cnpc-ntrpg",
        name = "NT-RPG CustomNPCs Bridge",
        description = "Plugin that connects NT-RPG plugin and CustomNPCs mod and adds some useful stuff",
        version = "1.0",
        authors = {"GlassSpirit"},
        dependencies = {
                @Dependency(id = "nt-rpg"),
                @Dependency(id = "customnpcs")
        }
)
public class CnpcRpgSponge {

    public static boolean useMixins = false;

    public static Configuration configuration;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> config;

    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        configuration = new Configuration();
        loadConfig();
    }

    @Listener
    public void onGameReload(GameReloadEvent event) {
        loadConfig();
    }

    @Listener
    public void onGameAboutToStartServer(GameAboutToStartServerEvent event) {
        try {
            Class.forName("noppes.npcs.api.NpcAPI");
            logger.info("CustomNPCs found! Event listener registered.");
            if (useMixins) logger.info("Mixins are used, new features await!");
        } catch (ClassNotFoundException e) {
            logger.error("CustomNPCs not found! Plugin will not work!", e);
            return;
        }
        new CustomNPCsEventListener();
        new Commands();
    }

    private void loadConfig() {
        try {
            ObjectMapper.BoundInstance configMapper = ObjectMapper.forObject(configuration);
            CommentedConfigurationNode node = config.load();
            configMapper.populate(node);
            config.save(node);
        } catch (Exception e) {
            logger.error("Could not load config", e);
        }
    }
}