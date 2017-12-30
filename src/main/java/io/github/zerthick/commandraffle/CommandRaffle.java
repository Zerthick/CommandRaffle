package io.github.zerthick.commandraffle;

import com.google.inject.Inject;
import io.github.zerthick.commandraffle.cmd.CommandRegister;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleManager;
import io.github.zerthick.commandraffle.raffle.RaffleResult;
import io.github.zerthick.commandraffle.util.config.PluginConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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

    private RaffleManager raffleManager;
    private PluginConfig pluginConfig;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        raffleManager = new RaffleManager(new HashMap<>());

        pluginConfig = new PluginConfig(null);

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

    public void processRaffleResult(Raffle raffle, RaffleResult raffleResult) {
        Optional<UUID> winnerOptional = raffleResult.getWinner();
        if (winnerOptional.isPresent()) {
            UUID winnerUUID = winnerOptional.get();
            UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            userStorageService.get(winnerUUID).ifPresent(user -> {
                String command = raffle.getCmd().replaceAll("\\{Winner\\}", user.getName());
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                fetchOnlineRafflePlayers(raffle).forEach(player -> player.sendMessage(Text.of(TextColors.YELLOW,
                        "The winner of the ", TextColors.GOLD, raffle.getName(),
                        TextColors.YELLOW, " raffle is ", TextColors.GOLD, user.getName(),
                        TextColors.YELLOW, "!")));
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
                processRaffleResult(r, result);
                if (r.isRepeating()) {
                    r.reset();
                } else {
                    raffleManager.removeRaffle(r.getName());
                }
            });
        }
    }
}
