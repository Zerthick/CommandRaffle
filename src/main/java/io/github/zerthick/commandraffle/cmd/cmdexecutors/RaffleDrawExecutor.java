package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.cmd.CommandArgs;
import io.github.zerthick.commandraffle.raffle.Raffle;
import io.github.zerthick.commandraffle.raffle.RaffleResult;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RaffleDrawExecutor extends AbstractCmdExecutor {

    public RaffleDrawExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        Optional<String> raffleNameOptional = args.getOne(CommandArgs.RAFFLE_NAME);

        if (raffleNameOptional.isPresent()) {
            String raffleName = raffleNameOptional.get();
            Optional<Raffle> raffleOptional = raffleManager.removeRaffle(raffleName);
            if (raffleOptional.isPresent()) {
                Raffle raffle = raffleOptional.get();
                RaffleResult raffleResult = raffle.draw();
                plugin.processRaffleResult(raffle, raffleResult);
                src.sendMessage(Text.of(TextColors.YELLOW, raffleName, " has been drawn!"));
            } else {
                src.sendMessage(Text.of(TextColors.RED, raffleName, " is not a raffle!"));
            }
        }

        return CommandResult.success();
    }
}
