package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.cmd.CommandArgs;
import io.github.zerthick.commandraffle.raffle.Raffle;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.time.Duration;
import java.util.Optional;

public class RaffleCreateExecutor extends AbstractCmdExecutor {

    public RaffleCreateExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        Optional<String> raffleNameOptional = args.getOne(CommandArgs.RAFFLE_NAME);
        Optional<String> raffleCmdOptional = args.getOne(CommandArgs.RAFFLE_CMD);
        Optional<Duration> raffleDurationOptional = args.getOne(CommandArgs.RAFFLE_DURATION);

        if (raffleNameOptional.isPresent() &&
                raffleCmdOptional.isPresent() &&
                raffleDurationOptional.isPresent()) {
            String raffleName = raffleNameOptional.get();
            String raffleCmd = raffleCmdOptional.get();
            Duration duration = raffleDurationOptional.get();

            if (duration.isNegative()) {
                src.sendMessage(Text.of(TextColors.RED, "Raffle duration must not be negative!"));
                return CommandResult.success();
            }

            int numTickets = (int) args.getOne(CommandArgs.RAFFLE_NUM_TICKETS).orElse(pluginConfig.getDefaultNumTickets());
            double ticketPrice = (double) args.getOne(CommandArgs.RAFFLE_TICKET_PRICE).orElse(pluginConfig.getDefaultTicketPrice());
            int ticketLimit = (int) args.getOne(CommandArgs.RAFFLE_TICKET_LIMIT).orElse(-1);

            boolean repeating = args.hasAny("r");

            String permNode = (String) args.getOne(CommandArgs.RAFFLE_PERM).orElse("");

            Text description = TextSerializers.FORMATTING_CODE.deserialize(
                    (String) args.getOne(CommandArgs.RAFFLE_DESC).orElse(raffleCmd));

            if (raffleManager.isRaffle(raffleName)) {
                src.sendMessage(Text.of(TextColors.RED, "A raffle with the name ", raffleName, " already exists!"));
            } else {
                Raffle newRaffle = new Raffle(raffleName, raffleCmd, duration, numTickets, ticketPrice, ticketLimit, repeating, permNode, description);
                raffleManager.addRaffle(newRaffle);
                src.sendMessage(Text.of(TextColors.YELLOW, raffleName, " raffle created!"));
            }
        }

        return CommandResult.success();
    }
}
