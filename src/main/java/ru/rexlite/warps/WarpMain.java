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

package ru.rexlite.warps;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import ru.rexlite.warps.commands.DelWarpCommand;
import ru.rexlite.warps.commands.SetWarpCommand;
import ru.rexlite.warps.commands.WarpCommand;
import ru.rexlite.warps.commands.WarpsCommand;
import ru.rexlite.warps.forms.WarpFormHandler;
import ru.rexlite.warps.managers.ConfigManager;
import ru.rexlite.warps.managers.WarpManager;

public class WarpMain extends PluginBase implements Listener {

    public static WarpManager warpManager;
    public static WarpFormHandler formHandler;
    public static ConfigManager configManager;
    private static WarpMain instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getDataFolder().mkdirs();
        warpManager = new WarpManager(getDataFolder());
        configManager = new ConfigManager(this);
        formHandler = new WarpFormHandler(this);
        getServer().getPluginManager().registerEvents(formHandler, this);

        // Register commands
        registerCommands();

        this.getLogger().info(" ");
        this.getLogger().info(TextFormat.AQUA + "FormWarps " + TextFormat.DARK_AQUA + "enabled!");
        this.getLogger().info(TextFormat.AQUA + "Plugin from: " + TextFormat.DARK_AQUA + "https://cloudburstmc.org/resources/formwarps.1072/");
        this.getLogger().info(" ");
    }

    private void registerCommands() {
        getServer().getCommandMap().register("formwarps", new SetWarpCommand(configManager.commandConfigs.get("setwarp")));
        getServer().getCommandMap().register("formwarps", new DelWarpCommand(configManager.commandConfigs.get("delwarp")));
        getServer().getCommandMap().register("formwarps", new WarpCommand(configManager.commandConfigs.get("warp")));
        getServer().getCommandMap().register("formwarps", new WarpsCommand(configManager.commandConfigs.get("warps")));
    }

    public static WarpMain getInstance() {
        return instance;
    }
}
