package io.github.zerthick.commandraffle.raffle;

import java.util.Optional;
import java.util.UUID;

public class RaffleResult {

    private UUID winner;
    private Raffle raffle;

    public RaffleResult(Raffle raffle) {
        this.raffle = raffle;
        winner = null;
    }

    public RaffleResult(Raffle raffle, UUID winner) {
        this.raffle = raffle;
        this.winner = winner;
    }

    public Optional<UUID> getWinner() {
        return Optional.ofNullable(winner);
    }

    public Raffle getRaffle() {
        return raffle;
    }
}
