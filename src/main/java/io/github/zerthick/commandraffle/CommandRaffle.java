package io.github.zerthick.commandraffle;

import com.google.inject.Inject;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleManager;
import io.github.zerthick.commandraffle.raffle.RaffleResult;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;

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


    private RaffleManager raffleManager;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        raffleManager = new RaffleManager(new HashMap<>());

        Task.builder()
                .interval(1, TimeUnit.MINUTES)
                .execute(new RaffleUpdateTask())
                .submit(this);
    }

    private class RaffleUpdateTask implements Consumer<Task> {

        @Override
        public void accept(Task task) {
            Collection<Raffle> expiredRaffles = new ArrayList<>(raffleManager.getExpiredRaffles(Instant.now()));
            expiredRaffles.forEach(r -> {
                RaffleResult result = r.draw();

                Optional<UUID> winnerOptional = result.getWinner();
                if (winnerOptional.isPresent()) {
                    UUID winnerUUID = winnerOptional.get();
                    UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
                    userStorageService.get(winnerUUID).ifPresent(user -> {
                        String command = r.getCmd().replaceAll("\\{Winner\\}", user.getName());
                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                    });
                }

                if (r.isRepeating()) {
                    r.reset();
                } else {
                    raffleManager.removeRaffle(r.getName());
                }
            });
        }
    }
}
