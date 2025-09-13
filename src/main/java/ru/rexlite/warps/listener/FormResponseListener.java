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

package ru.rexlite.warps.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import ru.rexlite.warps.WarpMain;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormResponseListener implements Listener {

    private static final int SET_WARP_FORM = 8562;
    private static final int DELETE_WARP_FORM = 8563;
    private static final int CONFIRM_FORM = 8564;
    private static final int WARP_FORM = 8565;
    private static final int ALL_WARPS_FORM = 8566;
    private static final int WARP_INFO_FORM = 8567;

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (event.wasClosed()) return;

        if (event.getWindow() instanceof FormWindowCustom form) {
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            if (response == null) return;
            String title = form.getTitle();

            if (title.equals(WarpMain.configService.getFormSetwarpTitle())) {
                String warpName = response.getInputResponse(0);
                if (warpName != null) WarpMain.formHandler.setWarp(player, warpName.trim());
            }

            if (title.equals(WarpMain.configService.getFormWarpTitle())) {
                String warpName = response.getInputResponse(0);
                if (warpName != null) WarpMain.formHandler.teleportToWarp(player, warpName.trim());
            }
        }

        if (event.getWindow() instanceof FormWindowSimple form) {
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if (response == null) return;

            String title = form.getTitle();
            int index = response.getClickedButtonId();

            if (title.equals(WarpMain.configService.getFormDeletewarpTitle())) {
                boolean seeAll = player.hasPermission("formwarps.commands.delwarp.others");
                Map<String, String> warps = seeAll
                        ? WarpMain.warpService.getAllWarpsWithOwners()
                        : new LinkedHashMap<>();

                if (!seeAll) {
                    for (String warp : WarpMain.warpService.getPlayerWarps(player.getName()).keySet()) {
                        warps.put(warp, player.getName());
                    }
                }

                if (index < warps.size()) {
                    String warpName = (String) warps.keySet().toArray()[index];
                    String owner = warps.get(warpName);
                    WarpMain.formHandler.getConfirmMap().put(player.getName(), warpName + ":" + owner);

                    FormWindowSimple confirm = new FormWindowSimple(
                            WarpMain.configService.getFormConfirmTitle(),
                            WarpMain.configService.replace(WarpMain.configService.getFormConfirmDesc(), "warp", warpName)
                    );
                    confirm.addButton(new ElementButton(WarpMain.configService.getMsgYes()));
                    confirm.addButton(new ElementButton(WarpMain.configService.getMsgNo()));
                    player.showFormWindow(confirm, CONFIRM_FORM);
                }
            }

            if (title.equals(WarpMain.configService.getFormConfirmTitle())) {
                String data = WarpMain.formHandler.getConfirmMap().remove(player.getName());
                if (response.getClickedButtonId() == 0 && data != null) {
                    String[] parts = data.split(":", 2);
                    WarpMain.formHandler.deleteWarp(player, parts[0], parts[1]);
                }
            }

            if (title.equals(WarpMain.configService.getFormWarpsTitle())) {
                Map<String, String> allWarps = WarpMain.warpService.getAllWarpsWithOwners();
                Map<String, String> opWarps = new LinkedHashMap<>();
                Map<String, String> otherWarps = new LinkedHashMap<>();

                for (Map.Entry<String, String> entry : allWarps.entrySet()) {
                    String warpName = entry.getKey();
                    String owner = entry.getValue();
                    Player ownerPlayer = WarpMain.getInstance().getServer().getPlayerExact(owner);
                    boolean isOp = ownerPlayer != null && ownerPlayer.isOp();

                    if (isOp) {
                        opWarps.put(warpName, owner);
                    } else {
                        otherWarps.put(warpName, owner);
                    }
                }

                Map<String, String> sortedWarps = new LinkedHashMap<>();
                sortedWarps.putAll(opWarps);
                sortedWarps.putAll(otherWarps);

                if (index < sortedWarps.size()) {
                    String warpName = (String) sortedWarps.keySet().toArray()[index];
                    String owner = sortedWarps.get(warpName);
                    WarpMain.formHandler.showWarpInfoForm(player, warpName, owner);
                }
            }

            if (title.equals(WarpMain.configService.getFormWarpInfoTitle())) {
                String data = WarpMain.formHandler.getWarpInfoMap().get(player.getName());
                if (data == null) return;

                String[] parts = data.split(":", 2);
                String warpName = parts[0];
                String owner = parts[1];

                if (index == 0) {
                    WarpMain.formHandler.teleportToWarp(player, warpName);
                } else if (index == 1 && (player.hasPermission("formwarps.commands.delwarp.others") || player.getName().equals(owner))) {
                    WarpMain.formHandler.getConfirmMap().put(player.getName(), warpName + ":" + owner);
                    FormWindowSimple confirm = new FormWindowSimple(
                            WarpMain.configService.getFormConfirmTitle(),
                            WarpMain.configService.replace(WarpMain.configService.getFormConfirmDesc(), "warp", warpName)
                    );
                    confirm.addButton(new ElementButton(WarpMain.configService.getMsgYes()));
                    confirm.addButton(new ElementButton(WarpMain.configService.getMsgNo()));
                    player.showFormWindow(confirm, CONFIRM_FORM);
                } else {
                    WarpMain.formHandler.getWarpInfoMap().remove(player.getName());
                    WarpMain.formHandler.showWarpsForm(player);
                }
            }
        }
    }
}