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

import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;

public class CommandArgs {

    public static final LiteralText RAFFLE_NAME = Text.of("RaffleName");
    public static final LiteralText RAFFLE_DURATION = Text.of("RaffleDuration");
    public static final LiteralText RAFFLE_CMD = Text.of("RaffleCmd");
    public static final LiteralText RAFFLE_NUM_TICKETS = Text.of("RaffleNumTickets");
    public static final LiteralText RAFFLE_TICKET_PRICE = Text.of("RaffleTicketPrice");
    public static final LiteralText RAFFLE_TICKET_LIMIT = Text.of("RaffleTicketLimit");
    public static final LiteralText RAFFLE_TICKET_AMOUNT = Text.of("RaffleTicketAmount");
    public static final LiteralText RAFFLE_PERM = Text.of("RafflePerm");
    public static final LiteralText RAFFLE_DESC = Text.of("RaffleDesc");

}
