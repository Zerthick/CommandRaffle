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

package io.github.zerthick.commandraffle.cmd;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.cmd.cmdexecutors.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;

public class CommandRegister {

    public static void registerCommands(CommandRaffle plugin) {

        CommandSpec create = CommandSpec.builder()
                .permission("commandraffle.command.create")
                .executor(new RaffleCreateExecutor(plugin))
                .arguments(GenericArguments.flags()
                        .flag("r", "-repeat")
                        .valueFlag(GenericArguments.integer(CommandArgs.RAFFLE_NUM_TICKETS), "n", "-numtickets")
                        .valueFlag(GenericArguments.doubleNum(CommandArgs.RAFFLE_TICKET_PRICE), "p", "-ticketprice")
                        .valueFlag(GenericArguments.integer(CommandArgs.RAFFLE_TICKET_LIMIT), "l", "-ticketlimit")
                        .valueFlag(GenericArguments.string(CommandArgs.RAFFLE_PERM), "p", "-perm")
                        .valueFlag(GenericArguments.string(CommandArgs.RAFFLE_DESC), "d", "-desc")
                        .buildWith(GenericArguments.seq(GenericArguments.string(CommandArgs.RAFFLE_NAME),
                                GenericArguments.string(CommandArgs.RAFFLE_CMD),
                                GenericArguments.duration(CommandArgs.RAFFLE_DURATION))))
                .build();

        CommandSpec buy = CommandSpec.builder()
                .permission("commandraffle.command.buy")
                .executor(new RaffleBuyExecutor(plugin))
                .arguments(GenericArguments.string(CommandArgs.RAFFLE_NAME),
                        GenericArguments.integer(CommandArgs.RAFFLE_TICKET_AMOUNT))
                .build();

        CommandSpec cancel = CommandSpec.builder()
                .permission("commandraffle.command.cancel")
                .executor(new RaffleCancelExecutor(plugin))
                .arguments(GenericArguments.string(CommandArgs.RAFFLE_NAME))
                .build();

        CommandSpec draw = CommandSpec.builder()
                .permission("commandraffle.command.draw")
                .executor(new RaffleDrawExecutor(plugin))
                .arguments(GenericArguments.string(CommandArgs.RAFFLE_NAME))
                .build();

        CommandSpec list = CommandSpec.builder()
                .permission("commandraffle.command.list")
                .executor(new RaffleListExecutor(plugin))
                .build();

        CommandSpec me = CommandSpec.builder()
                .permission("commandraffle.command.me")
                .executor(new RaffleMeExecutor(plugin))
                .build();

        CommandSpec raffle = CommandSpec.builder()
                .permission("commandraffle.command.info")
                .executor(new RaffleExecutor(plugin))
                .child(me, "me")
                .child(list, "list", "ls")
                .child(draw, "draw", "dr")
                .child(cancel, "cancel", "cn")
                .child(buy, "buy", "by")
                .child(create, "create", "cr")
                .build();

        Sponge.getCommandManager().register(plugin, raffle, "raffle", "rf");
    }

}
