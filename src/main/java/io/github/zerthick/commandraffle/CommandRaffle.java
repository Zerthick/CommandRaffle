package io.github.zerthick.commandraffle;

import com.google.inject.Inject;
import io.github.zerthick.commandraffle.cmd.CommandRegister;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleManager;
import io.github.zerthick.commandraffle.raffle.RaffleResult;
import io.github.zerthick.commandraffle.util.config.PluginConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Plugin(
        id = "commandraffle",
        name = "CommandRaffle",
        version = "0.0.1",
        description = "A simple plugin to raffle commands.",
        authors = {
                "Zerthick"
        }
)
public class CommandRaffle {

    @Inject
    private Logger logger;
    @Inject
    private PluginContainer instance;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfigDir;

    private RaffleManager raffleManager;
    private RewardCache rewardCache;
    private PluginConfig pluginConfig;

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(defaultConfig).build();

        //Generate default config if it doesn't exist
        if (!defaultConfig.toFile().exists()) {
            Asset defaultConfigAsset = getInstance().getAsset("DefaultConfig.conf").get();
            try {
                defaultConfigAsset.copyToFile(defaultConfig);
                configLoader.save(configLoader.load());
            } catch (IOException e) {
                logger.warn("Error loading default config! Error: " + e.getMessage());
            }
        }

        try {
            CommentedConfigurationNode configNode = configLoader.load();
            pluginConfig = new PluginConfig(configNode);
        } catch (IOException e) {
            logger.warn("Error loading config! Error: " + e.getMessage());
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        raffleManager = new RaffleManager(new HashMap<>());
        rewardCache = new RewardCache(new HashMap<>());

        CommandRegister.registerCommands(this);

        Task.builder()
                .interval(1, TimeUnit.MINUTES)
                .execute(new RaffleUpdateTask())
                .submit(this);

        // Log Start Up to Console
        logger.info(
                instance.getName() + " version " + instance.getVersion().orElse("")
                        + " enabled!");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        rewardCache.fetchRewards(player.getUniqueId())
                .ifPresent(cmdlist -> cmdlist.forEach(cmd ->
                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd)
                ));
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getInstance() {
        return instance;
    }

    public RaffleManager getRaffleManager() {
        return raffleManager;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public void processRaffleResult(RaffleResult raffleResult) {
        Optional<UUID> winnerOptional = raffleResult.getWinner();
        Raffle raffle = raffleResult.getRaffle();
        if (winnerOptional.isPresent()) {
            UUID winnerUUID = winnerOptional.get();
            UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            userStorageService.get(winnerUUID).ifPresent(user -> {
                String command = raffle.getCmd().replaceAll("\\{Winner\\}", user.getName());

                if (user.isOnline()) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                } else {
                    rewardCache.pushReward(winnerUUID, command);
                }

                fetchOnlineRafflePlayers(raffle).forEach(player -> {

                    if (player.getUniqueId().equals(winnerUUID)) {
                        player.sendMessage(Text.of(TextColors.YELLOW,
                                "You won the ", TextColors.GOLD, raffle.getName(),
                                TextColors.YELLOW, " raffle!"));
                    } else {
                        player.sendMessage(Text.of(TextColors.YELLOW,
                                "The winner of the ", TextColors.GOLD, raffle.getName(),
                                TextColors.YELLOW, " raffle is ", TextColors.GOLD, user.getName(),
                                TextColors.YELLOW, "!"));
                    }
                });
            });
        } else {
            fetchOnlineRafflePlayers(raffle).forEach(player -> player.sendMessage(Text.of(TextColors.YELLOW,
                    "The ", TextColors.GOLD, raffle.getName(),
                    TextColors.YELLOW, " raffle has no winner!")));
        }
    }

    private Set<Player> fetchOnlineRafflePlayers(Raffle raffle) {
        Set<Player> playerSet = new HashSet<>();
        UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        for (UUID uuid : raffle.getPlayerIDs()) {
            userStorageService.get(uuid)
                    .ifPresent(user -> user.getPlayer().ifPresent(playerSet::add));
        }
        return playerSet;
    }

    private class RaffleUpdateTask implements Consumer<Task> {

        @Override
        public void accept(Task task) {
            Collection<Raffle> expiredRaffles = new ArrayList<>(raffleManager.getExpiredRaffles(Instant.now()));
            expiredRaffles.forEach(r -> {
                RaffleResult result = r.draw();
                processRaffleResult(result);
                if (r.isRepeating()) {
                    r.reset();
                } else {
                    raffleManager.removeRaffle(r.getName());
                }
            });
        }
    }
}
