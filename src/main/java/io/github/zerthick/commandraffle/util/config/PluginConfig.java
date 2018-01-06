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

package io.github.zerthick.commandraffle.util.config;

import ninja.leaping.configurate.ConfigurationNode;

public class PluginConfig {

    private int defaultNumTickets;
    private double defaultTicketPrice;

    private boolean broadcastWinner;
    private String createBroadcast;
    private String winnerMessage;
    private String noWinnerMessage;

    public PluginConfig(ConfigurationNode node) {
        ConfigurationNode raffleDefaultsNode = node.getNode("RaffleDefaults");
        defaultNumTickets = raffleDefaultsNode.getNode("NumberOfTickets").getInt();
        defaultTicketPrice = raffleDefaultsNode.getNode("TicketCost").getDouble();

        broadcastWinner = node.getNode("BroadcastWinner").getBoolean();

        ConfigurationNode messagesNode = node.getNode("Messages");
        createBroadcast = messagesNode.getNode("CreateBroadcast").getString("");
        winnerMessage = messagesNode.getNode("WinnerMessage").getString("");
        noWinnerMessage = messagesNode.getNode("NoWinnerMessage").getString("");
    }


    public int getDefaultNumTickets() {
        return defaultNumTickets;
    }

    public double getDefaultTicketPrice() {
        return defaultTicketPrice;
    }

    public boolean isBroadcastWinner() {
        return broadcastWinner;
    }

    public String getCreateBroadcast() {
        return createBroadcast;
    }

    public String getWinnerMessage() {
        return winnerMessage;
    }

    public String getNoWinnerMessage() {
        return noWinnerMessage;
    }
}
