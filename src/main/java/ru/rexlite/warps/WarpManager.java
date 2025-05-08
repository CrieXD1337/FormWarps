package ru.rexlite.warps;

import cn.nukkit.utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WarpManager {

    private final Map<String, Map<String, double[]>> warps = new HashMap<>();
    private final Config config;

    public WarpManager(File dataFolder) {
        this.config = new Config(new File(dataFolder, "warps.yml"), Config.YAML);
        loadWarps();
    }

    @SuppressWarnings("unchecked")
    private void loadWarps() {
        for (String player : config.getKeys(false)) {
            warps.put(player, (Map<String, double[]>) config.get(player));
        }
    }

    public void save() {
        for (Map.Entry<String, Map<String, double[]>> entry : warps.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save();
    }

    public Map<String, double[]> getPlayerWarps(String player) {
        return warps.computeIfAbsent(player, k -> new HashMap<>());
    }

    public boolean warpExists(String player, String warpName) {
        return getPlayerWarps(player).containsKey(warpName);
    }

    public void addWarp(String player, String warpName, double[] coords) {
        getPlayerWarps(player).put(warpName, coords);
        save();
    }

    public boolean removeWarp(String player, String warpName) {
        Map<String, double[]> playerWarps = getPlayerWarps(player);
        if (playerWarps.containsKey(warpName)) {
            playerWarps.remove(warpName);
            save();
            return true;
        }
        return false;
    }

    public double[] getWarp(String player, String warpName) {
        return getPlayerWarps(player).get(warpName);
    }
}
