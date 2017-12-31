package io.github.zerthick.commandraffle.util.config;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.RewardCache;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleManager;
import io.github.zerthick.commandraffle.util.config.serializers.RaffleSerializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {

    public static void registerSerializers() {
        RaffleSerializer.register();
    }

    public static PluginConfig loadPluginConfig(CommandRaffle plugin) {

        PluginConfig pluginConfig = null;

        Path defaultConfig = plugin.getDefaultConfig();
        Logger logger = plugin.getLogger();

        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(defaultConfig).build();

        //Generate default config if it doesn't exist
        if (!defaultConfig.toFile().exists()) {
            Asset defaultConfigAsset = plugin.getInstance().getAsset("DefaultConfig.conf").get();
            try {
                defaultConfigAsset.copyToFile(defaultConfig);
                configLoader.save(configLoader.load());
            } catch (IOException e) {
                logger.error("Error loading default config! Error: " + e.getMessage());
            }
        }

        try {
            CommentedConfigurationNode configNode = configLoader.load();
            pluginConfig = new PluginConfig(configNode);
        } catch (IOException e) {
            logger.error("Error loading config! Error: " + e.getMessage());
        }

        return pluginConfig;
    }

    public static RewardCache loadRewardCache(CommandRaffle plugin) {

        File rewardCacheFile = new File(plugin.getDefaultConfigDir().toFile(), "rewardsCache.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(rewardCacheFile).build();

        Logger logger = plugin.getLogger();

        if (rewardCacheFile.exists()) {
            try {
                CommentedConfigurationNode rewardsNode = loader.load();
                Map<UUID, List<String>> rewardsMap = rewardsNode.getValue(new TypeToken<Map<UUID, List<String>>>() {
                }, new HashMap<>());
                return new RewardCache(rewardsMap);
            } catch (IOException | ObjectMappingException e) {
                logger.error("Error loading rewards cache! Error: " + e.getMessage());
            }
        }
        return new RewardCache(new HashMap<>());
    }

    public static void saveRewardCache(CommandRaffle plugin) {

        File rewardCacheFile = new File(plugin.getDefaultConfigDir().toFile(), "rewardsCache.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(rewardCacheFile).build();

        Logger logger = plugin.getLogger();

        try {
            CommentedConfigurationNode rewardsNode = loader.load();
            rewardsNode.setValue(new TypeToken<Map<UUID, List<String>>>() {
            }, plugin.getRewardCache().getRewardMap());
            loader.save(rewardsNode);
        } catch (IOException | ObjectMappingException e) {
            logger.error("Error saving rewards cache! Error: " + e.getMessage());
        }

    }

    public static RaffleManager loadRaffleManager(CommandRaffle plugin) {

        File rafflesFile = new File(plugin.getDefaultConfigDir().toFile(), "raffles.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(rafflesFile).build();

        Logger logger = plugin.getLogger();

        if (rafflesFile.exists()) {
            try {
                CommentedConfigurationNode rafflesNode = loader.load();
                Map<String, Raffle> rafflesMap = rafflesNode.getValue(new TypeToken<Map<String, Raffle>>() {
                }, new HashMap<>());
                return new RaffleManager(rafflesMap);
            } catch (IOException | ObjectMappingException e) {
                logger.error("Error loading raffles! Error: " + e.getMessage());
            }
        }
        return new RaffleManager(new HashMap<>());
    }

    public static void saveRaffleManager(CommandRaffle plugin) {

        File rafflesFile = new File(plugin.getDefaultConfigDir().toFile(), "raffles.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(rafflesFile).build();

        Logger logger = plugin.getLogger();

        try {
            CommentedConfigurationNode rafflesNode = loader.load();
            rafflesNode.setValue(new TypeToken<Map<String, Raffle>>() {
            }, plugin.getRaffleManager().getRaffleMap());
            loader.save(rafflesNode);
        } catch (IOException | ObjectMappingException e) {
            logger.error("Error saving raffles! Error: " + e.getMessage());
        }

    }
}
