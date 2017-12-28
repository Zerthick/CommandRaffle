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

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
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
                .async()
                .execute(new RaffleUpdateTask(this))
                .submit(this);
    }

    private class RaffleUpdateTask implements Consumer<Task> {

        private CommandRaffle plugin;

        public RaffleUpdateTask(CommandRaffle plugin) {
            this.plugin = plugin;
        }

        @Override
        public void accept(Task task) {
            Collection<Raffle> expiredRaffles = raffleManager.getExpiredRaffles(Instant.now());
            expiredRaffles.forEach(r -> {
                RaffleResult result = r.draw();

                Optional<UUID> winnerOptional = result.getWinner();
                if (winnerOptional.isPresent()) {
                    UUID winnerUUID = winnerOptional.get();

                    Task.builder()
                            .execute(() -> Sponge.getServer().getGameProfileManager().get(winnerUUID)
                                    .thenAccept(gameProfile -> {
                                        String command = r.getCmd().replaceAll("\\{Winner\\}", gameProfile.getName().get());
                                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                                    }))
                            .submit(plugin);
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
