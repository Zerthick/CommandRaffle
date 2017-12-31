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
