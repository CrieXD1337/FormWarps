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
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.*;
import cn.nukkit.form.window.*;
import cn.nukkit.level.Location;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WarpFormHandler implements Listener {

    private final Map<String, String> confirmMap = new HashMap<>();

    public WarpFormHandler(WarpMain plugin) {}

    public void showSetWarpForm(Player player) {
        FormWindowCustom form = new FormWindowCustom(WarpMain.configManager.formSetwarpTitle);
        form.addElement(new ElementInput(WarpMain.configManager.formSetwarpInput, WarpMain.configManager.formTipWarp));
        player.showFormWindow(form, 1);
    }

    public void showWarpForm(Player player) {
        FormWindowCustom form = new FormWindowCustom(WarpMain.configManager.formWarpTitle);
        form.addElement(new ElementInput(WarpMain.configManager.formWarpInput, WarpMain.configManager.formTipWarp));
        player.showFormWindow(form, 4);
    }

    public void setWarp(Player player, String warpName) {
        if (warpName.length() < 2 || warpName.length() > 14) {
            player.sendMessage(WarpMain.configManager.msgNameTooShort);
            return;
        }

        if (WarpMain.warpManager.warpExists(player.getName(), warpName)) {
            player.sendMessage(WarpMain.configManager.msgWarpExists);
            return;
        }

        WarpMain.warpManager.addWarp(player.getName(), warpName, player.getX(), player.getY(), player.getZ());
        player.sendMessage(WarpMain.configManager.replace(WarpMain.configManager.msgWarpSetSuccess, "warp", warpName));
    }

    public void showDeleteWarpForm(Player player) {
        boolean seeAll = player.hasPermission("formwarps.commands.delwarp.others");

        Map<String, String> warps = seeAll
                ? WarpMain.warpManager.getAllWarpsWithOwners()
                : new LinkedHashMap<>();

        if (!seeAll) {
            for (String warp : WarpMain.warpManager.getPlayerWarps(player.getName()).keySet()) {
                warps.put(warp, player.getName());
            }
        }

        FormWindowSimple form = new FormWindowSimple(
                WarpMain.configManager.formDeletewarpTitle,
                WarpMain.configManager.formDeletewarpDesc
        );

        if (warps.isEmpty()) {
            form.addButton(new ElementButton(WarpMain.configManager.msgNoWarps));
        } else {
            for (Map.Entry<String, String> entry : warps.entrySet()) {
                String label = seeAll ? entry.getKey() + " (" + entry.getValue() + ")" : entry.getKey();
                form.addButton(new ElementButton(label, new ElementButtonImageData("path", "textures/items/campfire")));
            }
        }

        player.showFormWindow(form, 2);
    }

    public void deleteWarp(Player player, String warpName, String owner) {
        if (!player.getName().equals(owner) && !player.hasPermission("formwarps.commands.delwarp.others")) {
            player.sendMessage(WarpMain.configManager.msgNoPermission);
            return;
        }

        if (WarpMain.warpManager.removeWarp(owner, warpName)) {
            player.sendMessage(WarpMain.configManager.replace(WarpMain.configManager.msgWarpDeleted, "warp", warpName));
        } else {
            player.sendMessage(WarpMain.configManager.msgWarpNotFound);
        }
    }

    public void teleportToWarp(Player player, String warpName) {
        String owner = WarpMain.warpManager.findOwnerOfWarp(warpName);
        if (owner == null) {
            player.sendMessage(WarpMain.configManager.msgWarpNotFound);
            return;
        }

        List<Double> coords = WarpMain.warpManager.getWarp(owner, warpName);
        if (coords == null || coords.size() != 3) {
            player.sendMessage(WarpMain.configManager.msgWarpNotFound);
            return;
        }

        player.teleport(new Location(coords.get(0), coords.get(1), coords.get(2), player.getLevel()));
        player.sendMessage(WarpMain.configManager.replace(WarpMain.configManager.msgWarpTpSuccess, "warp", warpName));
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (event.wasClosed()) return;

        if (event.getWindow() instanceof FormWindowCustom form) {
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            if (response == null) return;
            String title = form.getTitle();

            if (title.equals(WarpMain.configManager.formSetwarpTitle)) {
                String warpName = response.getInputResponse(0);
                if (warpName != null) setWarp(player, warpName.trim());
            }

            if (title.equals(WarpMain.configManager.formWarpTitle)) {
                String warpName = response.getInputResponse(0);
                if (warpName != null) teleportToWarp(player, warpName.trim());
            }
        }

        if (event.getWindow() instanceof FormWindowSimple form) {
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if (response == null) return;

            String title = form.getTitle();
            int index = response.getClickedButtonId();

            if (title.equals(WarpMain.configManager.formDeletewarpTitle)) {
                boolean seeAll = player.hasPermission("formwarps.commands.delwarp.others");
                Map<String, String> warps = seeAll
                        ? WarpMain.warpManager.getAllWarpsWithOwners()
                        : new LinkedHashMap<>();

                if (!seeAll) {
                    for (String warp : WarpMain.warpManager.getPlayerWarps(player.getName()).keySet()) {
                        warps.put(warp, player.getName());
                    }
                }

                if (index < warps.size()) {
                    String warpName = (String) warps.keySet().toArray()[index];
                    String owner = warps.get(warpName);
                    confirmMap.put(player.getName(), warpName + ":" + owner);

                    FormWindowSimple confirm = new FormWindowSimple(
                            WarpMain.configManager.formConfirmTitle,
                            WarpMain.configManager.replace(WarpMain.configManager.formConfirmDesc, "warp", warpName)
                    );
                    confirm.addButton(new ElementButton(WarpMain.configManager.msgYes));
                    confirm.addButton(new ElementButton(WarpMain.configManager.msgNo));
                    player.showFormWindow(confirm, 3);
                }
            }

            if (title.equals(WarpMain.configManager.formConfirmTitle)) {
                String data = confirmMap.remove(player.getName());
                if (response.getClickedButtonId() == 0 && data != null) {
                    String[] parts = data.split(":", 2);
                    deleteWarp(player, parts[0], parts[1]);
                }
            }
        }
    }
}
