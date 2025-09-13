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

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import ru.rexlite.warps.command.DelWarpCommand;
import ru.rexlite.warps.command.SetWarpCommand;
import ru.rexlite.warps.command.WarpCommand;
import ru.rexlite.warps.command.WarpsCommand;
import ru.rexlite.warps.form.WarpFormHandler;
import ru.rexlite.warps.listener.FormResponseListener;
import ru.rexlite.warps.service.ConfigService;
import ru.rexlite.warps.service.WarpService;

public class WarpMain extends PluginBase {

    @Getter
    private static WarpMain instance;
    @Getter
    public static WarpService warpService;
    @Getter
    public static WarpFormHandler formHandler;
    @Getter
    public static ConfigService configService;

    @Override
    public void onEnable() {
        instance = this;
        this.getDataFolder().mkdirs();
        warpService = new WarpService(getDataFolder());
        configService = new ConfigService(this);
        formHandler = new WarpFormHandler(this);
        getServer().getPluginManager().registerEvents(new FormResponseListener(), this);

        registerCommands();

        this.getLogger().info(" ");
        this.getLogger().info(TextFormat.AQUA + "FormWarps " + TextFormat.DARK_AQUA + "enabled!");
        this.getLogger().info(TextFormat.AQUA + "Plugin from: " + TextFormat.DARK_AQUA + "https://cloudburstmc.org/resources/formwarps.1072/");
        this.getLogger().info(" ");
    }

    private void registerCommands() {
        getServer().getCommandMap().register("formwarps", new SetWarpCommand(configService.getCommandConfig("setwarp")));
        getServer().getCommandMap().register("formwarps", new DelWarpCommand(configService.getCommandConfig("delwarp")));
        getServer().getCommandMap().register("formwarps", new WarpCommand(configService.getCommandConfig("warp")));
        getServer().getCommandMap().register("formwarps", new WarpsCommand(configService.getCommandConfig("warps")));
    }
}
