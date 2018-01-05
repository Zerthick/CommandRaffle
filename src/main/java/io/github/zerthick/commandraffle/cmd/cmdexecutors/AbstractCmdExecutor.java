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

package io.github.zerthick.commandraffle.cmd.cmdexecutors;

import io.github.zerthick.commandraffle.CommandRaffle;
import io.github.zerthick.commandraffle.raffle.RaffleManager;
import io.github.zerthick.commandraffle.util.config.PluginConfig;
import org.spongepowered.api.command.spec.CommandExecutor;

public abstract class AbstractCmdExecutor implements CommandExecutor {

    protected CommandRaffle plugin;
    protected RaffleManager raffleManager;
    protected PluginConfig pluginConfig;

    public AbstractCmdExecutor(CommandRaffle plugin) {
        this.plugin = plugin;
        raffleManager = plugin.getRaffleManager();
        pluginConfig = plugin.getPluginConfig();
    }
}
