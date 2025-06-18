/*
 * This file is part of FormWarps, licensed under the MIT License.
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

package ru.rexlite.warps.managers;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.util.*;

public class ConfigManager {

    private final Config config;
    public final Map<String, CommandConfig> commandConfigs = new HashMap<>();
    public int minCreateWarpCharacters;
    public int maxCreateWarpCharacters;
    public String allowedWarpCharacters;

    // messages
    public String msgWarpUsage;
    public String msgWarpTpSuccess;
    public String msgWarpSetSuccess;
    public String msgWarpExists;
    public String msgWarpNotFound;
    public String msgWarpDeleted;
    public String msgNoPermission;
    public String msgNameTooShort;
    public String msgNameInvalidCharacters;
    public String msgNoWarps;
    public String formSetwarpTitle;
    public String formSetwarpInput;
    public String formDeletewarpTitle;
    public String formDeletewarpDesc;
    public String formConfirmTitle;
    public String formConfirmDesc;
    public String formWarpTitle;
    public String formWarpInput;
    public String formTipWarp;
    public String formWarpsTitle;
    public String formWarpsDesc;
    public String formWarpInfoTitle;
    public String formWarpInfoDesc;
    public String formWarpInfoTeleport;
    public String formWarpInfoRemove;
    public String formWarpInfoBack;
    public String msgYes;
    public String msgNo;

    public ConfigManager(PluginBase plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        // Load command configurations
        loadCommandConfigs();

        // Load properties
        minCreateWarpCharacters = config.getInt("properties.min-create-warp-characters", 2);
        maxCreateWarpCharacters = config.getInt("properties.max-create-warp-characters", 14);
        allowedWarpCharacters = config.getString("properties.create-warp-allowed-characters", "^[a-zA-Z0-9_]+$");

        // Validate regex
        try {
            "".matches(allowedWarpCharacters);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid regex in create-warp-allowed-characters: " + allowedWarpCharacters + ". Falling back to default.");
            allowedWarpCharacters = "^[a-zA-Z0-9_]+$";
        }

        // messages
        msgWarpUsage = get("warp-usage", "§7> §cUsage: §e/warp <warp-name>");
        msgWarpTpSuccess = get("warp-tp-success", "§7> §fTeleported to warp §b{warp}");
        msgWarpSetSuccess = get("warp-set-success", "§7> §fWarp §b{warp} §fcreated successfully!");
        msgWarpExists = get("warp-exists", "§7> This warp already exists!");
        msgWarpNotFound = get("warp-not-found", "§7> §cWarp not found!");
        msgWarpDeleted = get("warp-deleted", "§7> §fWarp §b{warp} §fdeleted successfully.");
        msgNoPermission = get("no-permission", "§c%commands.generic.permission");
        msgNameTooShort = get("name-too-short", "§7> §cWarp name must be between §e{min}§c and §e{max}§c characters.");
        msgNameInvalidCharacters = get("name-invalid-characters", "§7> §cWarp name contains invalid characters. Allowed: {allowed}");
        msgNoWarps = get("no-warps", "You have no warps.");

        // forms
        formSetwarpTitle = get("form-setwarp-title", "Create Warp Point");
        formSetwarpInput = get("form-setwarp-input", "Enter warp name ({min}-{max} characters, allowed: {allowed}):");
        formDeletewarpTitle = get("form-deletewarp-title", "Delete Warp");
        formDeletewarpDesc = get("form-deletewarp-desc", "Select a warp to delete:");
        formConfirmTitle = get("form-confirm-title", "Confirm Deletion");
        formConfirmDesc = get("form-confirm-desc", "Delete warp {warp}?");
        formWarpTitle = get("form-warp-title", "Teleport to warp");
        formWarpInput = get("form-warp-input", "Enter warp name:");
        formTipWarp = get("form-tip-warp", "warp name");
        formWarpsTitle = get("warps-title", "Server Warps");
        formWarpsDesc = get("form-warps-desc", "Select a warp:");
        formWarpInfoTitle = get("form-warpinfo-title", "Warp Info");
        formWarpInfoDesc = get("form-warpinfo-desc", "Info about §e{warp}§f warp");
        formWarpInfoTeleport = get("form-warpinfo-teleport", "Teleport to warp");
        formWarpInfoRemove = get("form-warpinfo-remove", "Remove warp");
        formWarpInfoBack = get("form-warpinfo-back", "Back");
        msgYes = get("yes", "§2Yes");
        msgNo = get("no", "§cNo");
    }

    private void loadCommandConfigs() {
        Map<String, Object> commandsSection = config.getSection("commands");
        if (commandsSection == null) {
            commandsSection = new HashMap<>();
        }

        // Default command configs
        Map<String, CommandConfig> defaults = new HashMap<>();
        defaults.put("warp", new CommandConfig("warp", Arrays.asList("w"), "Teleport to a warp", "formwarps.commands.warp"));
        defaults.put("setwarp", new CommandConfig("setwarp", Arrays.asList("createwarp"), "Create a warp", "formwarps.commands.setwarp"));
        defaults.put("delwarp", new CommandConfig("delwarp", Arrays.asList("removewarp", "rmwarp"), "Delete a warp", "formwarps.commands.delwarp"));
        defaults.put("warps", new CommandConfig("warps", new ArrayList<>(), "See all warps", "formwarps.commands.warps"));

        for (String commandKey : defaults.keySet()) {
            if (commandsSection.containsKey(commandKey)) {
                Map<String, Object> cmdConfig = (Map<String, Object>) commandsSection.get(commandKey);
                String name = (String) cmdConfig.getOrDefault("name", defaults.get(commandKey).getName());
                List<String> aliases = (List<String>) cmdConfig.getOrDefault("aliases", defaults.get(commandKey).getAliases());
                String description = (String) cmdConfig.getOrDefault("description", defaults.get(commandKey).getDescription());
                String permission = (String) cmdConfig.getOrDefault("permission", defaults.get(commandKey).getPermission());
                commandConfigs.put(commandKey, new CommandConfig(name, aliases, description, permission));
            } else {
                commandConfigs.put(commandKey, defaults.get(commandKey));
            }
        }
    }

    private String get(String key, String def) {
        return TextFormat.colorize(config.getString("messages." + key, def));
    }

    public String replace(String msg, String key, String value) {
        return msg.replace("{" + key + "}", value);
    }

    public static class CommandConfig {
        private final String name;
        private final List<String> aliases;
        private final String description;
        private final String permission;

        public CommandConfig(String name, List<String> aliases, String description, String permission) {
            this.name = name;
            this.aliases = aliases;
            this.description = description;
            this.permission = permission;
        }

        public String getName() {
            return name;
        }

        public List<String> getAliases() {
            return aliases;
        }

        public String getDescription() {
            return description;
        }

        public String getPermission() {
            return permission;
        }
    }
}
