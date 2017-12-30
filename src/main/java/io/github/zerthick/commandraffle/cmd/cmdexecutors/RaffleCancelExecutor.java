package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.cmd.CommandArgs;
import io.github.zerthick.commandraffle.raffle.Raffle;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RaffleCancelExecutor extends AbstractCmdExecutor {

    public RaffleCancelExecutor(CommandRaffle plugin) {
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
                raffle.refund();
                src.sendMessage(Text.of(TextColors.YELLOW, raffleName, " has been canceled!"));
            } else {
                src.sendMessage(Text.of(TextColors.RED, raffleName, " is not a raffle!"));
            }
        }

        return CommandResult.success();
    }
}
