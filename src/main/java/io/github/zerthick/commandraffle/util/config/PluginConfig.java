package io.github.zerthick.commandraffle.util.config;

import ninja.leaping.configurate.ConfigurationNode;

public class PluginConfig {

    private int defaultNumTickets;
    private double defaultTicketPrice;

    public PluginConfig(ConfigurationNode node) {
        ConfigurationNode raffleDefaultsNode = node.getNode("RaffleDefaults");
        defaultNumTickets = raffleDefaultsNode.getNode("NumberOfTickets").getInt();
        defaultTicketPrice = raffleDefaultsNode.getNode("TicketPrice").getDouble();
    }


    public int getDefaultNumTickets() {
        return defaultNumTickets;
    }

    public double getDefaultTicketPrice() {
        return defaultTicketPrice;
    }
}
