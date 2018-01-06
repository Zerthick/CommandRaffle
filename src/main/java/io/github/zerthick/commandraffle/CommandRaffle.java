/*
 * Copyright (C) 2018  Zerthick
 *
 * This file is part of CommandRaffle.
 *
 * CommandRaffle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * CommandRaffle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CommandRaffle.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.zerthick.commandraffle;

import com.google.inject.Inject;
import io.github.zerthick.commandraffle.cmd.CommandRegister;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleManager;
import io.github.zerthick.commandraffle.raffle.RaffleResult;
import io.github.zerthick.commandraffle.util.config.ConfigManager;
import io.github.zerthick.commandraffle.util.config.PluginConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Plugin(
        id = "commandraffle",
        name = "CommandRaffle",
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

        ConfigManager.registerSerializers();

        pluginConfig = ConfigManager.loadPluginConfig(this);
        rewardCache = ConfigManager.loadRewardCache(this);
        raffleManager = ConfigManager.loadRaffleManager(this);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

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

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        ConfigManager.saveRewardCache(this);
        ConfigManager.saveRaffleManager(this);
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

    public Path getDefaultConfig() {
        return defaultConfig;
    }

    public Path getDefaultConfigDir() {
        return defaultConfigDir;
    }

    public RewardCache getRewardCache() {
        return rewardCache;
    }

    public void processRaffleResult(RaffleResult raffleResult) {
        Optional<UUID> winnerOptional = raffleResult.getWinner();
        Raffle raffle = raffleResult.getRaffle();
        if (winnerOptional.isPresent()) {
            UUID winnerUUID = winnerOptional.get();
            UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            userStorageService.get(winnerUUID).ifPresent(user -> {
                String command = raffle.getCmd().replaceAll("\\{Winner}", user.getName());

                if (user.isOnline()) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                } else {
                    rewardCache.pushReward(winnerUUID, command);
                }

                Text winnerText = buildRaffleWinnerMsg(raffleResult);

                if(pluginConfig.isBroadcastWinner()) {
                    Sponge.getServer().getBroadcastChannel().send(getInstance(), winnerText);
                } else {
                    fetchOnlineRafflePlayers(raffle).forEach(player -> player.sendMessage(winnerText));
                }
            });
        } else {

            Text noWinnerText = buildRaffleNoWinnerMsg(raffleResult);

            if(pluginConfig.isBroadcastWinner()) {
                Sponge.getServer().getBroadcastChannel().send(getInstance(), noWinnerText);
            } else {
                fetchOnlineRafflePlayers(raffle).forEach(player -> player.sendMessage(noWinnerText));
            }
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

    public Text buildRaffleCreateMsg(Raffle raffle) {
        String raffleCreateMessage = pluginConfig.getCreateBroadcast();
        raffleCreateMessage = raffleCreateMessage.replaceAll("\\{Raffle_Name}", raffle.getName());

        return TextSerializers.FORMATTING_CODE.deserialize(raffleCreateMessage);
    }

    private Text buildRaffleWinnerMsg(RaffleResult raffleResult) {
        String raffleWinnerMessage = pluginConfig.getWinnerMessage();

        UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        String winnerName = userStorageService.get(raffleResult.getWinner().get()).get().getName();

        raffleWinnerMessage = raffleWinnerMessage
                .replaceAll("\\{Raffle_Name}", raffleResult.getRaffle().getName())
                .replaceAll("\\{Winner}", winnerName);

        return TextSerializers.FORMATTING_CODE.deserialize(raffleWinnerMessage);
    }

    private Text buildRaffleNoWinnerMsg(RaffleResult raffleResult) {
        String raffleNoWinnerMessage = pluginConfig.getNoWinnerMessage();
        raffleNoWinnerMessage = raffleNoWinnerMessage.replaceAll("\\{Raffle_Name}", raffleResult.getRaffle().getName());

        return TextSerializers.FORMATTING_CODE.deserialize(raffleNoWinnerMessage);
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
