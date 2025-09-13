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

package ru.rexlite.warps.service;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class ConfigService {

    private final Config config;
    private final Map<String, CommandConfig> commandConfigs = new HashMap<>();
    private int minCreateWarpCharacters;
    private int maxCreateWarpCharacters;
    private String allowedWarpCharacters;

    // Messages
    @Setter private String msgWarpUsage;
    @Setter private String msgWarpTpSuccess;
    @Setter private String msgWarpSetSuccess;
    @Setter private String msgWarpExists;
    @Setter private String msgWarpNotFound;
    @Setter private String msgWarpDeleted;
    @Setter private String msgNoPermission;
    @Setter private String msgNameTooShort;
    @Setter private String msgNameInvalidCharacters;
    @Setter private String msgNoWarps;
    @Setter private String formSetwarpTitle;
    @Setter private String formSetwarpInput;
    @Setter private String formDeletewarpTitle;
    @Setter private String formDeletewarpDesc;
    @Setter private String formConfirmTitle;
    @Setter private String formConfirmDesc;
    @Setter private String formWarpTitle;
    @Setter private String formWarpInput;
    @Setter private String formTipWarp;
    @Setter private String formWarpsTitle;
    @Setter private String formWarpsDesc;
    @Setter private String formWarpInfoTitle;
    @Setter private String formWarpInfoDesc;
    @Setter private String formWarpInfoTeleport;
    @Setter private String formWarpInfoRemove;
    @Setter private String formWarpInfoBack;
    @Setter private String msgYes;
    @Setter private String msgNo;

    public ConfigService(PluginBase plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        loadCommandConfigs();

        minCreateWarpCharacters = config.getInt("properties.min-create-warp-characters", 2);
        maxCreateWarpCharacters = config.getInt("properties.max-create-warp-characters", 14);
        allowedWarpCharacters = config.getString("properties.create-warp-allowed-characters", "^[a-zA-Z0-9_]+$");

        try {
            "".matches(allowedWarpCharacters);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid regex in create-warp-allowed-characters: " + allowedWarpCharacters + ". Falling back to default.");
            allowedWarpCharacters = "^[a-zA-Z0-9_]+$";
        }

        msgWarpUsage = getMessage("warp-usage", "&7> &cUsage: &e/warp <warp-name>");
        msgWarpTpSuccess = getMessage("warp-tp-success", "&7> &fTeleported to warp &b{warp}");
        msgWarpSetSuccess = getMessage("warp-set-success", "&7> &fWarp &b{warp} &fcreated successfully!");
        msgWarpExists = getMessage("warp-exists", "&7> This warp already exists!");
        msgWarpNotFound = getMessage("warp-not-found", "&7> &cWarp not found!");
        msgWarpDeleted = getMessage("warp-deleted", "&7> &fWarp &b{warp} &fdeleted successfully.");
        msgNoPermission = getMessage("no-permission", "&c%commands.generic.permission");
        msgNameTooShort = getMessage("name-too-short", "&7> &cWarp name must be between &e{min}&c and &e{max}&c characters.");
        msgNameInvalidCharacters = getMessage("name-invalid-characters", "&7> &cWarp name contains invalid characters. Allowed: {allowed}");
        msgNoWarps = getMessage("no-warps", "You have no warps.");

        formSetwarpTitle = getMessage("form-setwarp-title", "Create Warp Point");
        formSetwarpInput = getMessage("form-setwarp-input", "Enter warp name ({min}-{max} characters, allowed: {allowed}):");
        formDeletewarpTitle = getMessage("form-deletewarp-title", "Delete Warp");
        formDeletewarpDesc = getMessage("form-deletewarp-desc", "Select a warp to delete:");
        formConfirmTitle = getMessage("form-confirm-title", "Confirm Deletion");
        formConfirmDesc = getMessage("form-confirm-desc", "Delete warp {warp}?");
        formWarpTitle = getMessage("form-warp-title", "Teleport to warp");
        formWarpInput = getMessage("form-warp-input", "Enter warp name:");
        formTipWarp = getMessage("form-tip-warp", "warp name");
        formWarpsTitle = getMessage("warps-title", "Server Warps");
        formWarpsDesc = getMessage("form-warps-desc", "Select a warp:");
        formWarpInfoTitle = getMessage("form-warpinfo-title", "Warp Info");
        formWarpInfoDesc = getMessage("form-warpinfo-desc", "Info about &e{warp}&f warp");
        formWarpInfoTeleport = getMessage("form-warpinfo-teleport", "Teleport to warp");
        formWarpInfoRemove = getMessage("form-warpinfo-remove", "Remove warp");
        formWarpInfoBack = getMessage("form-warpinfo-back", "Back");
        msgYes = getMessage("yes", "&2Yes");
        msgNo = getMessage("no", "&cNo");
    }

    private void loadCommandConfigs() {
        Map<String, Object> commandsSection = config.getSection("commands");
        if (commandsSection == null) {
            commandsSection = new HashMap<>();
        }

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

    private String getMessage(String key, String def) {
        return TextFormat.colorize(config.getString("messages." + key, def));
    }

    private String get(String key, String def) {
        return TextFormat.colorize(config.getString(key, def));
    }

    public String replace(String msg, String key, String value) {
        return msg.replace("{" + key + "}", value);
    }

    public CommandConfig getCommandConfig(String key) {
        return commandConfigs.get(key);
    }

    @Getter
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
    }
}