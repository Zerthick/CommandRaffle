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

package io.github.zerthick.commandraffle.raffle;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RaffleManager {

    private Map<String, Raffle> raffleMap;

    public RaffleManager(Map<String, Raffle> raffleMap) {
        this.raffleMap = raffleMap;
    }

    public Collection<Raffle> getRaffles() {
        return raffleMap.values();
    }

    public SortedSet<Raffle> getSortedRaffles(Comparator<Raffle> comparator) {
        SortedSet<Raffle> sortedSet = new TreeSet<>(comparator);
        sortedSet.addAll(getRaffles());
        return sortedSet;
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

    public Map<String, Raffle> getRaffleMap() {
        return raffleMap;
    }
}
