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
import io.github.zerthick.commandraffle.raffle.RaffleResult;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RaffleDrawExecutor extends AbstractCmdExecutor {

    public RaffleDrawExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        Optional<String> raffleNameOptional = args.getOne(CommandArgs.RAFFLE_NAME);

        if (raffleNameOptional.isPresent()) {
            String raffleName = raffleNameOptional.get();
            Optional<Raffle> raffleOptional = raffleManager.getRaffle(raffleName);
            if (raffleOptional.isPresent()) {
                Raffle raffle = raffleOptional.get();
                RaffleResult raffleResult = raffle.draw();
                plugin.processRaffleResult(raffleResult);
                if (raffle.isRepeating()) {
                    raffle.reset();
                } else {
                    raffleManager.removeRaffle(raffleName);
                }
                src.sendMessage(Text.of(TextColors.YELLOW, raffleName, " has been drawn!"));
            } else {
                src.sendMessage(Text.of(TextColors.RED, raffleName, " is not a raffle!"));
            }
        }

        return CommandResult.success();
    }
}
