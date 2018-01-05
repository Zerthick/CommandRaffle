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

package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.util.RaffleTimeFormatter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RaffleListExecutor extends AbstractCmdExecutor {

    public RaffleListExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        EconomyService economyService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        Currency dc = economyService.getDefaultCurrency();

        Instant now = Instant.now();
        List<Text> content = raffleManager.getSortedRaffles(Comparator.comparing(Raffle::getDrawTime)).stream()
                .filter(raffle -> raffle.hasPermission(src))
                .map(raffle -> {
                    Text titleText = Text.builder()
                            .append(Text.of(TextColors.GOLD, raffle.getName()))
                            .onHover(TextActions.showText(raffle.getDescription()))
                            .build();
                    Text ticketsText = Text.of(raffle.getAvailableTickets(), " at ", dc.format(BigDecimal.valueOf(raffle.getTicketPrice())), "/ticket");
                    Text timeText = RaffleTimeFormatter.formatTimeText(raffle, now);

                    return Text.of(titleText, "  ", ticketsText, "  ", timeText);
                }).collect(Collectors.toList());

        PaginationList list = PaginationList.builder()
                .title(Text.of(TextColors.GOLD, ".oO Raffles Oo."))
                .header(Text.of(TextColors.YELLOW, "Name | Available Tickets/Ticket Price | Draw Time", Text.NEW_LINE))
                .contents(content)
                .padding(Text.of("_"))
                .build();

        list.sendTo(src);

        return CommandResult.success();
    }
}
