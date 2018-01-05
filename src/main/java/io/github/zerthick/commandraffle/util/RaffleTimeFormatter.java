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

package io.github.zerthick.commandraffle.util;

import io.github.zerthick.commandraffle.raffle.Raffle;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.api.text.Text;

import java.time.Duration;
import java.time.Instant;

public class RaffleTimeFormatter {

    public static Text formatTimeText(Raffle raffle, Instant now) {

        Instant drawTime = raffle.getDrawTime();

        if (drawTime.isAfter(now)) {
            Duration duration = Duration.between(now, drawTime);
            return Text.of(DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true));
        }

        return Text.of("Any Minute!");
    }
}
