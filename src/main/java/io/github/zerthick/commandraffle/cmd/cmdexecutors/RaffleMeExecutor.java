package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

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

            List<Text> content = raffleManager.getRaffles().stream()
                    .filter(raffle -> raffle.hasTicket(player))
                    .map(raffle -> {
                        Text titleText = Text.builder()
                                .append(Text.of(TextColors.YELLOW, raffle.getName()))
                                .onHover(TextActions.showText(raffle.getDescription()))
                                .build();
                        Text ticketsText = Text.of(raffle.getTicketCount(player));

                        return Text.of(titleText, "  ", ticketsText);
                    }).collect(Collectors.toList());

            PaginationList list = PaginationList.builder()
                    .title(Text.of("Raffles"))
                    .header(Text.of("Name | Bought Tickets"))
                    .contents(content)
                    .padding(Text.of("`"))
                    .build();

            list.sendTo(src);
        } else {
            src.sendMessage(Text.of("You must be a player to have tickets!"));
        }

        return CommandResult.success();
    }
}
