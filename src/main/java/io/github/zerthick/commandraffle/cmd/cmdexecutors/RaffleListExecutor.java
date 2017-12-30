package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.raffle.Raffle;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class RaffleListExecutor extends AbstractCmdExecutor {

    public RaffleListExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        EconomyService economyService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        Currency dc = economyService.getDefaultCurrency();

        Instant now = Instant.now();
        List<Text> content = raffleManager.getRaffles().stream()
                .filter(raffle -> raffle.hasPermission(src))
                .map(raffle -> {
                    Text titleText = Text.builder()
                            .append(Text.of(TextColors.GOLD, raffle.getName()))
                            .onHover(TextActions.showText(raffle.getDescription()))
                            .build();
                    Text ticketsText = Text.of(raffle.getAvailableTickets(), " at ", dc.format(BigDecimal.valueOf(raffle.getTicketPrice())), "/ticket");
                    Text timeText = formatTimeText(raffle, now);

                    return Text.of(titleText, "  ", ticketsText, "  ", timeText);
                }).collect(Collectors.toList());

        PaginationList list = PaginationList.builder()
                .title(Text.of(TextColors.GOLD, ".oO Raffles Oo."))
                .header(Text.of(TextColors.YELLOW, "Name | Available Tickets/Ticket Price | Draw Time", Text.NEW_LINE))
                .contents(content)
                .padding(Text.of("_"))
                .build();

        list.sendTo(src);

        return CommandResult.success();
    }

    private Text formatTimeText(Raffle raffle, Instant now) {

        Instant drawTime = raffle.getDrawTime();

        if (drawTime.isAfter(now)) {
            Duration duration = Duration.between(now, drawTime);
            return Text.of(DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true));
        }

        return Text.of("Any Minute!");
    }
}
