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
import io.github.zerthick.commandraffle.cmd.CommandArgs;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RaffleBuyExecutor extends AbstractCmdExecutor {

    public RaffleBuyExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<String> raffleNameOptional = args.getOne(CommandArgs.RAFFLE_NAME);
        Optional<Integer> ticketAmountOptional = args.getOne(CommandArgs.RAFFLE_TICKET_AMOUNT);

        if (src instanceof Player) {
            Player player = (Player) src;
            if (raffleNameOptional.isPresent() && ticketAmountOptional.isPresent()) {

                String raffleName = raffleNameOptional.get();
                int ticketAmount = ticketAmountOptional.get();

                if (ticketAmount <= 0) {
                    throw new CommandException(Text.of("Ticket amount must be positive!"));
                }

                Optional<Raffle> raffleOptional = raffleManager.getRaffle(raffleName);
                if (raffleOptional.isPresent()) {
                    Raffle raffle = raffleOptional.get();
                    try {
                        raffle.buyTicket(player, ticketAmount);
                    } catch (RaffleException e) {
                        throw new CommandException(Text.of(e.getMessage()));
                    }
                    src.sendMessage(Text.of(TextColors.YELLOW, "Successfully bought ", ticketAmount, " tickets!"));
                } else {
                    src.sendMessage(Text.of(TextColors.RED, raffleName, " is not a raffle!"));
                }
            }
        } else {
            src.sendMessage(Text.of("You must be a player to buy raffle tickets!"));
        }
        return CommandResult.success();
    }
}
