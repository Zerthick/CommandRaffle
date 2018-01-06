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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.time.Duration;
import java.util.Optional;

public class RaffleCreateExecutor extends AbstractCmdExecutor {

    public RaffleCreateExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<String> raffleNameOptional = args.getOne(CommandArgs.RAFFLE_NAME);
        Optional<String> raffleCmdOptional = args.getOne(CommandArgs.RAFFLE_CMD);
        Optional<Duration> raffleDurationOptional = args.getOne(CommandArgs.RAFFLE_DURATION);

        if (raffleNameOptional.isPresent() &&
                raffleCmdOptional.isPresent() &&
                raffleDurationOptional.isPresent()) {
            String raffleName = raffleNameOptional.get();
            String raffleCmd = raffleCmdOptional.get();
            Duration duration = raffleDurationOptional.get();

            if (duration.isNegative()) {
                src.sendMessage(Text.of(TextColors.RED, "Raffle duration must not be negative!"));
                return CommandResult.success();
            }

            int numTickets = (int) args.getOne(CommandArgs.RAFFLE_NUM_TICKETS).orElse(pluginConfig.getDefaultNumTickets());
            double ticketPrice = (double) args.getOne(CommandArgs.RAFFLE_TICKET_PRICE).orElse(pluginConfig.getDefaultTicketPrice());
            int ticketLimit = (int) args.getOne(CommandArgs.RAFFLE_TICKET_LIMIT).orElse(-1);

            if (numTickets <= 0) {
                throw new CommandException(Text.of("Ticket amount must be positive!"));
            }
            if (ticketPrice <= 0) {
                throw new CommandException(Text.of("Ticket price must be positive!"));
            }
            if (ticketLimit != -1 && ticketLimit <= 0) {
                throw new CommandException(Text.of("Ticket limit must be positive!"));
            }

            boolean repeating = args.hasAny("r");

            String permNode = (String) args.getOne(CommandArgs.RAFFLE_PERM).orElse("");

            Text description = TextSerializers.FORMATTING_CODE.deserialize(
                    (String) args.getOne(CommandArgs.RAFFLE_DESC).orElse(raffleCmd));

            if (raffleManager.isRaffle(raffleName)) {
                src.sendMessage(Text.of(TextColors.RED, "A raffle with the name ", raffleName, " already exists!"));
            } else {
                Raffle newRaffle = new Raffle(raffleName, raffleCmd, duration, numTickets, ticketPrice, ticketLimit, repeating, permNode, description);
                raffleManager.addRaffle(newRaffle);

                src.sendMessage(Text.of(TextColors.YELLOW, raffleName, " raffle created!"));

                Text createBroadCast = plugin.buildRaffleCreateMsg(newRaffle);
                if(!createBroadCast.isEmpty()) {
                    Sponge.getServer().getBroadcastChannel().send(plugin.getInstance(), createBroadCast);
                }
            }
        }

        return CommandResult.success();
    }
}
