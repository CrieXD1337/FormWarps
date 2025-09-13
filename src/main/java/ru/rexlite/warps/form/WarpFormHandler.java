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

package ru.rexlite.warps.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.*;
import cn.nukkit.level.Location;
import ru.rexlite.warps.WarpMain;
import ru.rexlite.warps.exception.WarpException;

import java.util.*;

public class WarpFormHandler {

    private static final int SET_WARP_FORM = 8562;
    private static final int DELETE_WARP_FORM = 8563;
    private static final int CONFIRM_FORM = 8564;
    private static final int WARP_FORM = 8565;
    private static final int ALL_WARPS_FORM = 8566;
    private static final int WARP_INFO_FORM = 8567;

    private final Map<String, String> confirmMap = new HashMap<>();
    private final Map<String, String> warpInfoMap = new HashMap<>();

    public WarpFormHandler(WarpMain main) {}

    public void showSetWarpForm(Player player) {
        FormWindowCustom form = new FormWindowCustom(WarpMain.configService.getFormSetwarpTitle());
        String inputText = WarpMain.configService.replace(
                WarpMain.configService.replace(
                        WarpMain.configService.replace(
                                WarpMain.configService.getFormSetwarpInput(),
                                "min", String.valueOf(WarpMain.configService.getMinCreateWarpCharacters())
                        ),
                        "max", String.valueOf(WarpMain.configService.getMaxCreateWarpCharacters())
                ),
                "allowed", WarpMain.configService.getAllowedWarpCharacters()
        );
        form.addElement(new ElementInput(inputText, WarpMain.configService.getFormTipWarp()));
        player.showFormWindow(form, SET_WARP_FORM);
    }

    public void showWarpForm(Player player) {
        FormWindowCustom form = new FormWindowCustom(WarpMain.configService.getFormWarpTitle());
        form.addElement(new ElementInput(WarpMain.configService.getFormWarpInput(), WarpMain.configService.getFormTipWarp()));
        player.showFormWindow(form, WARP_FORM);
    }

    public void showDeleteWarpForm(Player player) {
        boolean seeAll = player.hasPermission("formwarps.commands.delwarp.others");
        Map<String, String> warps = seeAll
                ? WarpMain.warpService.getAllWarpsWithOwners()
                : new LinkedHashMap<>();

        if (!seeAll) {
            for (String warp : WarpMain.warpService.getPlayerWarps(player.getName()).keySet()) {
                warps.put(warp, player.getName());
            }
        }

        FormWindowSimple form = new FormWindowSimple(
                WarpMain.configService.getFormDeletewarpTitle(),
                WarpMain.configService.getFormDeletewarpDesc()
        );

        if (warps.isEmpty()) {
            form.addButton(new ElementButton(WarpMain.configService.getMsgNoWarps()));
        } else {
            for (Map.Entry<String, String> entry : warps.entrySet()) {
                String label = seeAll ? entry.getKey() + "\n (" + entry.getValue() + ")" : entry.getKey();
                form.addButton(new ElementButton(label, new ElementButtonImageData("path", "textures/items/campfire")));
            }
        }

        player.showFormWindow(form, DELETE_WARP_FORM);
    }

    public void showWarpsForm(Player player) {
        FormWindowSimple form = new FormWindowSimple(
                WarpMain.configService.getFormWarpsTitle(),
                WarpMain.configService.getFormWarpsDesc()
        );

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

        if (sortedWarps.isEmpty()) {
            form.addButton(new ElementButton(WarpMain.configService.getMsgNoWarps()));
        } else {
            for (Map.Entry<String, String> entry : sortedWarps.entrySet()) {
                String label = entry.getKey() + "\n (" + entry.getValue() + ")";
                form.addButton(new ElementButton(label, new ElementButtonImageData("path", "textures/items/campfire")));
            }
        }

        player.showFormWindow(form, ALL_WARPS_FORM);
    }

    public void showWarpInfoForm(Player player, String warpName, String owner) {
        FormWindowSimple form = new FormWindowSimple(
                WarpMain.configService.getFormWarpInfoTitle(),
                WarpMain.configService.replace(WarpMain.configService.getFormWarpInfoDesc(), "warp", warpName)
        );
        form.addButton(new ElementButton(WarpMain.configService.getFormWarpInfoTeleport(), new ElementButtonImageData("path", "textures/ui/realmsIcon")));
        if (player.hasPermission("formwarps.commands.delwarp.others") || player.getName().equals(owner)) {
            form.addButton(new ElementButton(WarpMain.configService.getFormWarpInfoRemove(), new ElementButtonImageData("path", "textures/blocks/barrier")));
        }
        form.addButton(new ElementButton(WarpMain.configService.getFormWarpInfoBack()));
        warpInfoMap.put(player.getName(), warpName + ":" + owner);
        player.showFormWindow(form, WARP_INFO_FORM);
    }

    public void setWarp(Player player, String warpName) {
        try {
            if (warpName.length() < WarpMain.configService.getMinCreateWarpCharacters() || warpName.length() > WarpMain.configService.getMaxCreateWarpCharacters()) {
                throw new WarpException(WarpMain.configService.getMsgNameTooShort());
            }
            if (!warpName.matches(WarpMain.configService.getAllowedWarpCharacters())) {
                throw new WarpException(WarpMain.configService.getMsgNameInvalidCharacters());
            }
            if (WarpMain.warpService.warpExists(player.getName(), warpName)) {
                throw new WarpException(WarpMain.configService.getMsgWarpExists());
            }

            WarpMain.warpService.addWarp(player.getName(), warpName, player.getX(), player.getY(), player.getZ());
            player.sendMessage(WarpMain.configService.replace(WarpMain.configService.getMsgWarpSetSuccess(), "warp", warpName));
        } catch (WarpException e) {
            player.sendMessage(e.getMessage());
        }
    }

    public void deleteWarp(Player player, String warpName, String owner) {
        if (WarpMain.warpService.removeWarp(owner, warpName)) {
            player.sendMessage(WarpMain.configService.replace(WarpMain.configService.getMsgWarpDeleted(), "warp", warpName));
        } else {
            player.sendMessage(WarpMain.configService.getMsgWarpNotFound());
        }
    }

    public void teleportToWarp(Player player, String warpName) {
        String owner = WarpMain.warpService.findOwnerOfWarp(warpName);
        if (owner == null) {
            player.sendMessage(WarpMain.configService.getMsgWarpNotFound());
            return;
        }

        List<Double> coords = WarpMain.warpService.getWarp(owner, warpName);
        if (coords == null || coords.size() != 3) {
            player.sendMessage(WarpMain.configService.getMsgWarpNotFound());
            return;
        }

        player.teleport(new Location(coords.get(0), coords.get(1), coords.get(2), player.getLevel()));
        player.sendMessage(WarpMain.configService.replace(WarpMain.configService.getMsgWarpTpSuccess(), "warp", warpName));
    }

    public Map<String, String> getConfirmMap() {
        return confirmMap;
    }

    public Map<String, String> getWarpInfoMap() {
        return warpInfoMap;
    }
}