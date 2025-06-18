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

package ru.rexlite.warps.forms;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.*;
import cn.nukkit.form.window.*;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import ru.rexlite.warps.WarpMain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WarpFormHandler implements Listener {

    private static final int SET_WARP_FORM = 8562;
    private static final int DELETE_WARP_FORM = 8563;
    private static final int CONFIRM_FORM = 8564;
    private static final int WARP_FORM = 8565;
    private static final int ALL_WARPS_FORM = 8566;
    private static final int WARP_INFO_FORM = 8567;

    private final Map<String, String> confirmMap = new HashMap<>();

    public WarpFormHandler(WarpMain plugin) {}

    public void showSetWarpForm(Player player) {
        FormWindowCustom form = new FormWindowCustom(WarpMain.configManager.formSetwarpTitle);
        String inputText = WarpMain.configManager.replace(
                WarpMain.configManager.replace(
                        WarpMain.configManager.replace(
                                WarpMain.configManager.formSetwarpInput,
                                "min", String.valueOf(WarpMain.configManager.minCreateWarpCharacters)
                        ),
                        "max", String.valueOf(WarpMain.configManager.maxCreateWarpCharacters)
                ),
                "allowed", WarpMain.configManager.allowedWarpCharacters
        );
        form.addElement(new ElementInput(inputText, WarpMain.configManager.formTipWarp));
        player.showFormWindow(form, SET_WARP_FORM);
    }

    public void showWarpForm(Player player) {
        FormWindowCustom form = new FormWindowCustom(WarpMain.configManager.formWarpTitle);
        form.addElement(new ElementInput(WarpMain.configManager.formWarpInput, WarpMain.configManager.formTipWarp));
        player.showFormWindow(form, WARP_FORM);
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
                String label = seeAll ? entry.getKey() + "\n (" + entry.getValue() + ")" : entry.getKey();
                form.addButton(new ElementButton(label, new ElementButtonImageData("path", "textures/items/campfire")));
            }
        }

        player.showFormWindow(form, DELETE_WARP_FORM);
    }

    private final Map<String, String> warpInfoMap = new HashMap<>();

    public void showWarpsForm(Player player) {
        FormWindowSimple form = new FormWindowSimple(
                WarpMain.configManager.formWarpsTitle,
                WarpMain.configManager.formWarpsDesc
        );

        Map<String, String> allWarps = WarpMain.warpManager.getAllWarpsWithOwners();
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
            form.addButton(new ElementButton(WarpMain.configManager.msgNoWarps));
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
                WarpMain.configManager.formWarpInfoTitle,
                WarpMain.configManager.replace(WarpMain.configManager.formWarpInfoDesc, "warp", warpName)
        );

        warpInfoMap.put(player.getName(), warpName + ":" + owner);

        form.addButton(new ElementButton(
                WarpMain.configManager.formWarpInfoTeleport,
                new ElementButtonImageData("path", "textures/ui/icon_import.png")
        ));

        if (player.hasPermission("formwarps.commands.delwarp.others") || player.getName().equals(owner)) {
            form.addButton(new ElementButton(
                    WarpMain.configManager.formWarpInfoRemove,
                    new ElementButtonImageData("path", "textures/ui/cancel.png")
            ));
        }

        form.addButton(new ElementButton(
                WarpMain.configManager.formWarpInfoBack,
                new ElementButtonImageData("path", "textures/ui/arrow_left.png")
        ));

        player.showFormWindow(form, WARP_INFO_FORM);
    }

    public void setWarp(Player player, String warpName) {
        int minLength = WarpMain.configManager.minCreateWarpCharacters;
        int maxLength = WarpMain.configManager.maxCreateWarpCharacters;
        String allowedChars = WarpMain.configManager.allowedWarpCharacters;

        if (warpName.length() < minLength || warpName.length() > maxLength) {
            player.sendMessage(WarpMain.configManager.replace(
                    WarpMain.configManager.replace(
                            WarpMain.configManager.msgNameTooShort,
                            "min", String.valueOf(minLength)
                    ),
                    "max", String.valueOf(maxLength)
            ));
            return;
        }

        if (!warpName.matches(allowedChars)) {
            player.sendMessage(WarpMain.configManager.replace(
                    WarpMain.configManager.msgNameInvalidCharacters,
                    "allowed", allowedChars
            ));
            return;
        }

        if (WarpMain.warpManager.warpExists(player.getName(), warpName)) {
            player.sendMessage(WarpMain.configManager.msgWarpExists);
            return;
        }

        WarpMain.warpManager.addWarp(player.getName(), warpName, player.getX(), player.getY(), player.getZ());
        player.sendMessage(TextFormat.colorize(WarpMain.configManager.replace(WarpMain.configManager.msgWarpSetSuccess, "warp", warpName)));
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
                    player.showFormWindow(confirm, CONFIRM_FORM);
                }
            }

            if (title.equals(WarpMain.configManager.formConfirmTitle)) {
                String data = confirmMap.remove(player.getName());
                if (response.getClickedButtonId() == 0 && data != null) {
                    String[] parts = data.split(":", 2);
                    deleteWarp(player, parts[0], parts[1]);
                }
            }

            if (title.equals(WarpMain.configManager.formWarpsTitle)) {
                Map<String, String> allWarps = WarpMain.warpManager.getAllWarpsWithOwners();
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
                    showWarpInfoForm(player, warpName, owner);
                }
            }

            if (title.equals(WarpMain.configManager.formWarpInfoTitle)) {
                String data = warpInfoMap.get(player.getName());
                if (data == null) return;

                String[] parts = data.split(":", 2);
                String warpName = parts[0];
                String owner = parts[1];

                if (index == 0) {
                    teleportToWarp(player, warpName);
                } else if (index == 1 && (player.hasPermission("formwarps.commands.delwarp.others") || player.getName().equals(owner))) {
                    confirmMap.put(player.getName(), warpName + ":" + owner);
                    FormWindowSimple confirm = new FormWindowSimple(
                            WarpMain.configManager.formConfirmTitle,
                            WarpMain.configManager.replace(WarpMain.configManager.formConfirmDesc, "warp", warpName)
                    );
                    confirm.addButton(new ElementButton(WarpMain.configManager.msgYes));
                    confirm.addButton(new ElementButton(WarpMain.configManager.msgNo));
                    player.showFormWindow(confirm, CONFIRM_FORM);
                } else {
                    warpInfoMap.remove(player.getName());
                    showWarpsForm(player);
                }
            }
        }
    }
}
