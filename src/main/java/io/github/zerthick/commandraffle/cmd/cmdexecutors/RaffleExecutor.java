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
import io.github.zerthick.commandraffle.cmd.Permissions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class RaffleExecutor extends AbstractCmdExecutor {

    public RaffleExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        Text raffle = Text.builder("/raffle")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle"))
                .build();

        Text raffleInfo = Text.builder("/raffle info")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle info"))
                .build();

        Text raffleList = Text.builder("/raffle list")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle list"))
                .build();

        Text raffleMe = Text.builder("/raffle me")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle me"))
                .build();

        Text raffleDraw = Text.builder("/raffle draw")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle draw"))
                .build();

        Text raffleCancel = Text.builder("/raffle cancel")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle cancel"))
                .build();

        Text raffleBuy = Text.builder("/raffle buy")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle buy"))
                .build();

        Text raffleCreate = Text.builder("/raffle create")
                .color(TextColors.GOLD)
                .onClick(TextActions.suggestCommand("/raffle create"))
                .build();

        List<Text> content = new ArrayList<>();

        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_HELP)) {
            content.add(Text.of(raffle, " - Display this help window Alias: rf"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_INFO)) {
            content.add(Text.of(raffleInfo, " - Display plugin version info Alias: if"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_LIST)) {
            content.add(Text.of(raffleList, " [Raffle] - Displays all raffles or the participants of [Raffle] Alias: ls"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_ME)) {
            content.add(Text.of(raffleMe, " - Displays all raffles you are currently in"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_DRAW)) {
            content.add(Text.of(raffleDraw, " <Raffle> - Forces <Raffle> to immediately draw a winner Alias: dr"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_CANCEL)) {
            content.add(Text.of(raffleCancel, " <Raffle> - Cancels <Raffle> refunding all bought tickets  Alias: cn"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_BUY)) {
            content.add(Text.of(raffleBuy, " <Raffle> <Amount> - Buys <Amount> of tickets in <Raffle> Alias: by"));
        }
        if (src.hasPermission(Permissions.COMMANDRAFFLE_COMMAND_CREATE)) {
            content.add(Text.of(raffleCreate, " <Raffle> <Command> <Duration> - Creates a new raffle with name <Raffle> reward command <Command> and duration <Duration> Alias: cr"));
        }

        PaginationList list = PaginationList.builder()
                .title(Text.of(TextColors.GOLD, ".oO Raffles Help Oo."))
                .contents(content)
                .padding(Text.of("_"))
                .build();

        list.sendTo(src);

        return CommandResult.success();
    }
}
