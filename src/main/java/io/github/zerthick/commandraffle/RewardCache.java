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

package io.github.zerthick.commandraffle;

import java.util.*;

public class RewardCache {

    private Map<UUID, List<String>> rewardMap;

    public RewardCache(Map<UUID, List<String>> rewardMap) {
        this.rewardMap = rewardMap;
    }

    public void pushReward(UUID playerUUID, String cmd) {
        List<String> rewards = rewardMap.getOrDefault(playerUUID, new ArrayList<>());
        rewards.add(cmd);
        rewardMap.put(playerUUID, rewards);
    }

    public Optional<List<String>> fetchRewards(UUID playerUUID) {
        return Optional.ofNullable(rewardMap.remove(playerUUID));
    }

    public Map<UUID, List<String>> getRewardMap() {
        return rewardMap;
    }
}
