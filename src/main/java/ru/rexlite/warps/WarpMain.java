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

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

public class WarpMain extends PluginBase implements Listener {

    public static WarpManager warpManager;
    public static WarpFormHandler formHandler;
    public static ConfigManager configManager;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();
        warpManager = new WarpManager(getDataFolder());
        configManager = new ConfigManager(this);
        formHandler = new WarpFormHandler(this);
        getServer().getPluginManager().registerEvents(formHandler, this);
        getLogger().info(TextFormat.AQUA + "FormWarps " + TextFormat.DARK_AQUA + "successfully enabled!");
        getLogger().info(TextFormat.DARK_AQUA + "Plugin from: " + TextFormat.DARK_AQUA + "https://cloudburstmc.org/resources/formwarps.1072/");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "This command is only available to players.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "setwarp":
                if (!player.hasPermission("formwarps.commands.setwarp")) {
                    player.sendMessage(configManager.msgNoPermission);
                    return true;
                }
                if (args.length == 1) {
                    formHandler.setWarp(player, args[0]);
                } else {
                    formHandler.showSetWarpForm(player);
                }
                break;

            case "delwarp":
                if (!player.hasPermission("formwarps.commands.delwarp")) {
                    player.sendMessage(configManager.msgNoPermission);
                    return true;
                }
                if (args.length == 1) {
                    formHandler.deleteWarp(player, args[0], player.getName());
                } else {
                    formHandler.showDeleteWarpForm(player);
                }
                break;

            case "warp":
                if (!player.hasPermission("formwarps.commands.warp")) {
                    player.sendMessage(configManager.msgNoPermission);
                    return true;
                }
                if (args.length == 1) {
                    formHandler.teleportToWarp(player, args[0]);
                } else {
                    formHandler.showWarpForm(player);
                }
                break;
        }

        return true;
    }
}
