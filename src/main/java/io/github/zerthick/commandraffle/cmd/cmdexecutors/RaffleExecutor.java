package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class RaffleExecutor extends AbstractCmdExecutor {
    public RaffleExecutor(CommandRaffle plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        PluginContainer container = plugin.getInstance();

        src.sendMessage(Text.of(TextColors.GOLD, container.getName(),
                TextColors.YELLOW, " version: ", TextColors.GOLD,
                container.getVersion().orElse(""), TextColors.YELLOW, " by ",
                TextColors.GOLD, "Zerthick"));

        return CommandResult.success();
    }
}
