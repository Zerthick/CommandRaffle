package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.util.RaffleTimeFormatter;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RaffleMeExecutor extends AbstractCmdExecutor {

    public RaffleMeExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        if (src instanceof Player) {

            Player player = (Player) src;

            Instant now = Instant.now();
            List<Text> content = raffleManager.getSortedRaffles(Comparator.comparing(Raffle::getDrawTime)).stream()
                    .filter(raffle -> raffle.hasTicket(player))
                    .map(raffle -> {
                        Text titleText = Text.builder()
                                .append(Text.of(TextColors.GOLD, raffle.getName()))
                                .onHover(TextActions.showText(raffle.getDescription()))
                                .build();
                        Text ticketsText = Text.of(raffle.getTicketCount(player));

                        Text timeText = RaffleTimeFormatter.formatTimeText(raffle, now);

                        return Text.of(titleText, "  ", ticketsText, "  ", timeText);
                    }).collect(Collectors.toList());

            PaginationList list = PaginationList.builder()
                    .title(Text.of(TextColors.GOLD, ".oO Raffles Oo."))
                    .header(Text.of(TextColors.YELLOW, "Name | Bought Tickets | Draw Time", Text.NEW_LINE))
                    .contents(content)
                    .padding(Text.of("_"))
                    .build();

            list.sendTo(src);
        } else {
            src.sendMessage(Text.of("You must be a player to have tickets!"));
        }

        return CommandResult.success();
    }
}
