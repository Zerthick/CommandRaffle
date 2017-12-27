package io.github.zerthick.commandraffle.raffle;

import java.util.Optional;
import java.util.UUID;

public class RaffleResult {

    private UUID winner;

    public RaffleResult() {
        winner = null;
    }

    public RaffleResult(UUID winner) {
        this.winner = winner;
    }

    public Optional<UUID> getWinner() {
        return Optional.ofNullable(winner);
    }
}
