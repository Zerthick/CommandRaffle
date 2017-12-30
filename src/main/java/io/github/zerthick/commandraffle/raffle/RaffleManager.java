package io.github.zerthick.commandraffle.raffle;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RaffleManager {

    private Map<String, Raffle> raffleMap;

    public RaffleManager(Map<String, Raffle> raffleMap) {
        this.raffleMap = raffleMap;
    }

    public Collection<Raffle> getRaffles() {
        return raffleMap.values();
    }

    public Optional<Raffle> getRaffle(String name) {
        return Optional.ofNullable(raffleMap.get(name.toUpperCase()));
    }

    public boolean isRaffle(String name) {
        return getRaffle(name).isPresent();
    }

    public void addRaffle(Raffle raffle) {
        raffleMap.put(raffle.getName().toUpperCase(), raffle);
    }

    public Optional<Raffle> removeRaffle(String name) {
        return Optional.ofNullable(raffleMap.remove(name.toUpperCase()));
    }

    public Collection<Raffle> getExpiredRaffles(Instant cutoff) {
        return raffleMap.values().stream()
                .filter(r -> cutoff.isAfter(r.getDrawTime())).collect(Collectors.toList());
    }
}
