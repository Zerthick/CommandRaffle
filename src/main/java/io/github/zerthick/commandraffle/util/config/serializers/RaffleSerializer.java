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

package io.github.zerthick.commandraffle.util.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.commandraffle.raffle.Raffle;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.text.Text;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class RaffleSerializer implements TypeSerializer<Raffle> {

    public static void register() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Raffle.class), new RaffleSerializer());
    }

    @Override
    public Raffle deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        String name = value.getNode("name").getString();
        String cmd = value.getNode("cmd").getString();
        Instant drawTime = Instant.ofEpochMilli(value.getNode("drawTime").getValue(TypeToken.of(Long.class)));
        Duration drawDuration = Duration.ofMillis(value.getNode("drawDuration").getLong());

        int numTickets = value.getNode("numTickets").getInt();
        double ticketPrice = value.getNode("ticketPrice").getDouble();
        int ticketLimit = value.getNode("ticketLimit").getInt();

        boolean repeating = value.getNode("repeating").getBoolean();

        String permNode = value.getNode("permNode").getString("");

        Text description = value.getNode("description").getValue(TypeToken.of(Text.class));

        Map<UUID, Integer> ticketMap = value.getNode("ticketMap").getValue(new TypeToken<Map<UUID, Integer>>() {
        });
        int availableTickets = value.getNode("availableTickets").getInt();


        return new Raffle(name,
                cmd,
                drawTime,
                drawDuration,
                numTickets,
                ticketPrice,
                ticketLimit,
                repeating,
                permNode,
                description,
                ticketMap,
                availableTickets);
    }

    @Override
    public void serialize(TypeToken<?> type, Raffle obj, ConfigurationNode value) throws ObjectMappingException {

        value.getNode("name").setValue(TypeToken.of(String.class), obj.getName());
        value.getNode("cmd").setValue(TypeToken.of(String.class), obj.getCmd());
        value.getNode("drawTime").setValue(TypeToken.of(Long.class), obj.getDrawTime().toEpochMilli());
        value.getNode("drawDuration").setValue(TypeToken.of(Long.class), obj.getDrawDuration().toMillis());

        value.getNode("numTickets").setValue(TypeToken.of(Integer.class), obj.getNumTickets());
        value.getNode("ticketPrice").setValue(TypeToken.of(Double.class), obj.getTicketPrice());
        value.getNode("ticketLimit").setValue(TypeToken.of(Integer.class), obj.getTicketLimit());

        value.getNode("repeating").setValue(TypeToken.of(Boolean.class), obj.isRepeating());

        value.getNode("permNode").setValue(TypeToken.of(String.class), obj.getPermNode());

        value.getNode("description").setValue(TypeToken.of(Text.class), obj.getDescription());

        value.getNode("ticketMap").setValue(new TypeToken<Map<UUID, Integer>>() {
        }, obj.getTicketMap());
        value.getNode("availableTickets").setValue(TypeToken.of(Integer.class), obj.getAvailableTickets());

    }
}
