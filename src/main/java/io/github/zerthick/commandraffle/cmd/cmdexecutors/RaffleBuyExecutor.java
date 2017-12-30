package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.cmd.CommandArgs;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RaffleBuyExecutor extends AbstractCmdExecutor {

    public RaffleBuyExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<String> raffleNameOptional = args.getOne(CommandArgs.RAFFLE_NAME);
        Optional<Integer> ticketAmountOptional = args.getOne(CommandArgs.RAFFLE_TICKET_AMOUNT);

        if (src instanceof Player) {
            Player player = (Player) src;
            if (raffleNameOptional.isPresent() && ticketAmountOptional.isPresent()) {

                String raffleName = raffleNameOptional.get();
                int ticketAmount = ticketAmountOptional.get();

                Optional<Raffle> raffleOptional = raffleManager.getRaffle(raffleName);
                if (raffleOptional.isPresent()) {
                    Raffle raffle = raffleOptional.get();
                    try {
                        raffle.buyTicket(player, ticketAmount);
                    } catch (RaffleException e) {
                        throw new CommandException(Text.of(e.getMessage()));
                    }
                    src.sendMessage(Text.of(TextColors.YELLOW, "Successfully bought ", ticketAmount, " tickets!"));
                } else {
                    src.sendMessage(Text.of(TextColors.RED, raffleName, " is not a raffle!"));
                }
            }
        } else {
            src.sendMessage(Text.of("You must be a player to buy raffle tickets!"));
        }
        return CommandResult.success();
    }
}
