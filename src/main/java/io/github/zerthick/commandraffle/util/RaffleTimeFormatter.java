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
