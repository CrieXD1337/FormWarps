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

package ru.rexlite.warps.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import ru.rexlite.warps.WarpMain;
import ru.rexlite.warps.form.WarpFormHandler;
import ru.rexlite.warps.service.ConfigService;

public class SetWarpCommand extends Command {

    public SetWarpCommand(ConfigService.CommandConfig config) {
        super(config.getName(), config.getDescription(), "/" + config.getName() + " [warpName]", config.getAliases().toArray(new String[0]));
        setPermission(config.getPermission());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        WarpFormHandler formHandler = WarpMain.getFormHandler();

        if (!testPermission(player)) {
            player.sendMessage(WarpMain.getConfigService().getMsgNoPermission());
            return true;
        }

        if (args.length == 1) {
            String warpName = args[0].trim();
            formHandler.setWarp(player, warpName);
        } else {
            formHandler.showSetWarpForm(player);
        }
        return true;
    }
}
