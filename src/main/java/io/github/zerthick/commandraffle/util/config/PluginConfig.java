package io.github.zerthick.commandraffle.util.config;

import ninja.leaping.configurate.ConfigurationNode;

public class PluginConfig {

    public PluginConfig(ConfigurationNode node) {

    }


    public int getDefaultNumTickets() {
        return 100;
    }

    public double getDefaultTicketPrice() {
        return 10.0;
    }
}
