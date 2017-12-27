package io.github.zerthick.commandraffle.raffle;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Raffle {

    private final String NAME;

    private Instant drawTime;

    private int numTickets;
    private double ticketPrice;
    private int ticketLimit;

    private String permNode;
    private String cmd;

    private Boolean repeating;

    private Text description;

    private Map<UUID, Integer> ticketMap;
    private int availableTickets;

    public Raffle(String NAME, Instant drawTime, int numTickets, double ticketPrice, int ticketLimit, String permNode, String cmd, Boolean repeating) {
        this.NAME = NAME;
        this.drawTime = drawTime;
        this.numTickets = numTickets;
        this.ticketPrice = ticketPrice;
        this.ticketLimit = ticketLimit;
        this.permNode = permNode;
        this.cmd = cmd;
        this.repeating = repeating;

        description = Text.EMPTY;
        ticketMap = new HashMap<>();
        availableTickets = numTickets;
    }

    public int getSoldTickets() {
        return ticketMap.values().stream().reduce(Integer::sum).orElse(0);
    }

    private ResultType econBuyTicket(UUID playerUUID, int amount) {
        EconomyService economyService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        Account playerAccount = economyService.getOrCreateAccount(playerUUID).get();

        return playerAccount.withdraw(economyService.getDefaultCurrency(),
                BigDecimal.valueOf(amount * ticketPrice), Cause.of(NamedCause.of("Raffle", this))).getResult();
    }

    public void buyTicket(UUID playerUUID, int amount) throws RaffleException {

        if(availableTickets > 0) {
            int boughtTickets = ticketMap.getOrDefault(playerUUID, 0);
            int totalTickets = boughtTickets + amount;

            if(ticketLimit != -1 && totalTickets <= ticketLimit) {
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
            return new RaffleResult();
        } else {
            return new RaffleResult(UUID.fromString(winner));
        }
    }

    public void refund() {
        EconomyService economyService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        ticketMap.forEach((k,v) -> economyService.getOrCreateAccount(k)
                .ifPresent(playerAccount ->
                        playerAccount.deposit(economyService.getDefaultCurrency(),
                                BigDecimal.valueOf(v * ticketPrice),
                                Cause.of(NamedCause.of("Raffle", this))
                        )
                )
        );
    }
}
