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

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class ConfigManager {

    private final Config config;

    public String msgWarpUsage;
    public String msgWarpTpSuccess;
    public String msgWarpSetSuccess;
    public String msgWarpExists;
    public String msgWarpNotFound;
    public String msgWarpDeleted;
    public String msgNoPermission;
    public String msgNameTooShort;
    public String msgNoWarps;
    public String formSetwarpTitle;
    public String formSetwarpInput;
    public String formDeletewarpTitle;
    public String formDeletewarpDesc;
    public String formConfirmTitle;
    public String formConfirmDesc;
    public String msgYes;
    public String msgNo;

    public ConfigManager(PluginBase plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        msgWarpUsage = getMessage("warp-usage", "§7> §cUsage: §e/warp <warp name>");
        msgWarpTpSuccess = getMessage("warp-tp-success", "§7> §fTeleported to warp §b{warp}");
        msgWarpSetSuccess = getMessage("warp-set-success", "§7> §fWarp §b{warp} §fcreated successfully!");
        msgWarpExists = getMessage("warp-exists", "§7> §cThis warp already exists!");
        msgWarpNotFound = getMessage("warp-not-found", "§7> §cWarp not found!");
        msgWarpDeleted = getMessage("warp-deleted", "§7> §fWarp §b{warp} §fdeleted successfully.");
        msgNoPermission = getMessage("no-permission", "§c%commands.genetic.permission");
        msgNameTooShort = getMessage("name-too-short", "§7> §cWarp name must be between §e2§c and §e14§c characters.");
        msgNoWarps = getMessage("no-warps", "You have no warps.");
        formSetwarpTitle = getMessage("form-setwarp-title", "Create Warp Point");
        formSetwarpInput = getMessage("form-setwarp-input", "Enter warp name (2-14 characters):");
        formDeletewarpTitle = getMessage("form-deletewarp-title", "Delete Warp");
        formDeletewarpDesc = getMessage("form-deletewarp-desc", "Select a warp to delete:");
        formConfirmTitle = getMessage("form-confirm-title", "Confirm Deletion");
        formConfirmDesc = getMessage("form-confirm-desc", "Delete warp {warp}?");
        msgYes = getMessage("yes", "Yes");
        msgNo = getMessage("no", "No");
    }

    private String getMessage(String path, String def) {
        return TextFormat.colorize(config.getString("messages." + path, def));
    }

    public String replace(String message, String key, String value) {
        return message.replace("{" + key + "}", value);
    }
}
