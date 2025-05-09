/*
 * This file is part of EssentialsChat, licensed under the MIT License.
 *
 *  Copyright (c) Ivan <CrieXD1337> <criex1337@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package ru.rexlite.warps;

import cn.nukkit.utils.Config;

import java.io.File;
import java.util.*;

public class WarpManager {

    private final Map<String, Map<String, List<Double>>> warps = new HashMap<>();
    private final Config config;

    public WarpManager(File dataFolder) {
        this.config = new Config(new File(dataFolder, "warps.yml"), Config.YAML);
        loadWarps();
    }

    @SuppressWarnings("unchecked")
    private void loadWarps() {
        for (String player : config.getKeys(false)) {
            Object raw = config.get(player);
            if (raw instanceof Map) {
                Map<String, Object> playerData = (Map<String, Object>) raw;
                Map<String, List<Double>> playerWarps = new HashMap<>();
                for (Map.Entry<String, Object> entry : playerData.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        List<Object> rawList = (List<Object>) entry.getValue();
                        List<Double> coords = new ArrayList<>();
                        for (Object o : rawList) {
                            coords.add(Double.parseDouble(o.toString()));
                        }
                        playerWarps.put(entry.getKey(), coords);
                    }
                }
                warps.put(player, playerWarps);
            }
        }
    }

    public void save() {
        for (Map.Entry<String, Map<String, List<Double>>> entry : warps.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save();
    }

    public Map<String, List<Double>> getPlayerWarps(String player) {
        return warps.computeIfAbsent(player, k -> new HashMap<>());
    }

    public boolean warpExists(String player, String warpName) {
        return getPlayerWarps(player).containsKey(warpName);
    }

    public void addWarp(String player, String warpName, double x, double y, double z) {
        getPlayerWarps(player).put(warpName, Arrays.asList(x, y, z));
        save();
    }

    public boolean removeWarp(String player, String warpName) {
        Map<String, List<Double>> playerWarps = getPlayerWarps(player);
        if (playerWarps.containsKey(warpName)) {
            playerWarps.remove(warpName);
            save();
            return true;
        }
        return false;
    }

    public List<Double> getWarp(String player, String warpName) {
        return getPlayerWarps(player).get(warpName);
    }

    public String findOwnerOfWarp(String warpName) {
        for (Map.Entry<String, Map<String, List<Double>>> entry : warps.entrySet()) {
            if (entry.getValue().containsKey(warpName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<String, String> getAllWarpsWithOwners() {
        Map<String, String> all = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, List<Double>>> entry : warps.entrySet()) {
            for (String warp : entry.getValue().keySet()) {
                all.put(warp, entry.getKey());
            }
        }
        return all;
    }
}
