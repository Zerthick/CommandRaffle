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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Raffle {

    private String name;
    private String cmd;
    private Instant drawTime;
    private Duration drawDuration;

    private int numTickets;
    private double ticketPrice;
    private int ticketLimit;

    private boolean repeating;

    private String permNode;

    private Text description;

    private Map<UUID, Integer> ticketMap;
    private int availableTickets;

    public Raffle(String name,
                  String cmd,
                  Instant drawTime,
                  Duration drawDuration,
                  int numTickets,
                  double ticketPrice,
                  int ticketLimit,
                  boolean repeating,
                  String permNode,
                  Text description,
                  Map<UUID, Integer> ticketMap,
                  int availableTickets) {
        this.name = name;
        this.cmd = cmd;
        this.drawTime = drawTime;
        this.drawDuration = drawDuration;
        this.numTickets = numTickets;
        this.ticketPrice = ticketPrice;
        this.ticketLimit = ticketLimit;
        this.repeating = repeating;
        this.permNode = permNode;
        this.description = description;
        this.ticketMap = ticketMap;
        this.availableTickets = availableTickets;
    }

    public Raffle(String name,
                  String cmd,
                  Duration drawDuration,
                  int numTickets,
                  double ticketPrice,
                  int ticketLimit,
                  boolean repeating,
                  String permNode,
                  Text description) {
        this.name = name;
        this.cmd = cmd;
        this.drawDuration = drawDuration;
        this.numTickets = numTickets;
        this.ticketPrice = ticketPrice;
        this.ticketLimit = ticketLimit;
        this.repeating = repeating;
        this.permNode = permNode;
        this.description = description;

        reset();
    }

    public void reset() {
        drawTime = Instant.now().plus(drawDuration.toMillis(), ChronoUnit.MILLIS);
        ticketMap = new HashMap<>();
        availableTickets = numTickets;
    }

    public int getSoldTickets() {
        return numTickets - availableTickets;
    }

    private ResultType econBuyTicket(UUID playerUUID, int amount) {
        EconomyService economyService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        Account playerAccount = economyService.getOrCreateAccount(playerUUID).get();

        return playerAccount.withdraw(economyService.getDefaultCurrency(),
                BigDecimal.valueOf(amount * ticketPrice), Cause.of(NamedCause.owner(this))).getResult();
    }

    public boolean hasPermission(Subject subject) {
        return permNode.isEmpty() || subject.hasPermission(permNode);
    }

    public boolean hasTicket(Player player) {
        return ticketMap.keySet().contains(player.getUniqueId());
    }

    public int getTicketCount(Player player) {
        return ticketMap.getOrDefault(player.getUniqueId(), 0);
    }

    public void buyTicket(Player player, int amount) throws RaffleException {

        UUID playerUUID = player.getUniqueId();

        if (!hasPermission(player)) {
            throw new RaffleException("You don't have permission to buy tickets from this raffle!");
        }

        if(availableTickets > 0) {
            int boughtTickets = ticketMap.getOrDefault(playerUUID, 0);
            int totalTickets = boughtTickets + amount;

            if (ticketLimit == -1 || totalTickets <= ticketLimit) {
                if(totalTickets <= availableTickets) {
                    if(econBuyTicket(playerUUID, amount) == ResultType.SUCCESS) {
                        ticketMap.put(playerUUID, totalTickets);
                        availableTickets -= amount;
                    } else {
                        throw new RaffleException("You don't have enough funds!");
                    }
                } else {
                    throw new RaffleException("This raffle only has " + availableTickets + " tickets available!");
                }
            } else {
                throw  new RaffleException("This raffle has a ticket limit of " + ticketLimit + "!");
            }
        } else {
            throw new RaffleException("This raffle is full!");
        }
    }

    public RaffleResult draw() {
        List<String> tickets = ticketMap.entrySet().stream().map(e -> Collections.nCopies(e.getValue(), e.getKey().toString())).flatMap(List::stream).collect(Collectors.toList());
        tickets.addAll(Collections.nCopies(availableTickets, "")); // Add in unpurchased tickets

        Collections.shuffle(tickets); // Randomize the list

        String winner = tickets.get(0);

        if(winner.isEmpty()) {
            return new RaffleResult(this);
        } else {
            return new RaffleResult(this, UUID.fromString(winner));
        }
    }

    public void refund() {
        EconomyService economyService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        ticketMap.forEach((k,v) -> economyService.getOrCreateAccount(k)
                .ifPresent(playerAccount ->
                        playerAccount.deposit(economyService.getDefaultCurrency(),
                                BigDecimal.valueOf(v * ticketPrice),
                                Cause.of(NamedCause.owner(this))
                        )
                )
        );
    }

    public String getName() {
        return name;
    }

    public Duration getDrawDuration() {
        return drawDuration;
    }

    public Instant getDrawTime() {
        return drawTime;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getTicketLimit() {
        return ticketLimit;
    }

    public String getPermNode() {
        return permNode;
    }

    public String getCmd() {
        return cmd;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public Text getDescription() {
        return description;
    }

    public Map<UUID, Integer> getTicketMap() {
        return ticketMap;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public Set<UUID> getPlayerIDs() {
        return ticketMap.keySet();
    }
}
