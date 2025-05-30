# FormWarps
**Plugin for warp system for Nukkit with GUI**

## Features
- Forms
- Simple config
- Creating warps
- Teleportation to warps
- Removing warps

## Commands
**/warp** - open warp UI

**/setwarp** - open setwarp UI

**/removewarp** - open remove warps UI

**/warps** - view all warps on the server (warp by OPs pinned)

## Permissions
- formarps
  - formwarps.commands
    - formwarps.commands.warp
    - formwarps.commands.warps
    - formwarps.commands.delwarp
      - formwarps.commands.delwarp.others
    - formwarps.commands.setwarp

## Config
```
messages:
  # Chat messages
  warp-usage: "§7> §cUsage: §e/warp <warp name>"
  warp-tp-success: "§7> §fTeleported to warp §b{warp}"
  warp-set-success: "§7> §fWarp §b{warp} §fcreated successfully!"
  warp-exists: "§7> §cThis warp already exists!"
  warp-not-found: "§7> §cWarp not found!"
  warp-deleted: "§7> §fWarp §b{warp} §fdeleted successfully."
  no-permission: "§c%commands.generic.permission"
  name-too-short: "§7> §cWarp name must be between §e2§c and §e14§c characters."

  # Forms text
  no-warps: "You have no warps."
  form-setwarp-title: "Create Warp Point"
  form-setwarp-input: "Enter warp name (2-14 characters):"
  form-deletewarp-title: "Delete Warp"
  form-deletewarp-desc: "Select a warp to delete:"
  form-confirm-title: "Confirm Deletion"
  form-confirm-desc: "Delete warp {warp}?"
  form-warp-tip: "warp name"
  form-warps-title: "Server Warps"
  form-warps-desc: "Select a warp:"
  form-warpinfo-title: "Warp Info"
  form-warpinfo-desc: "Info about §e{warp}§f warp"
  form-warpinfo-teleport: "Teleport to warp"
  form-warpinfo-remove: "Remove warp"
  form-warpinfo-back: "Back"
  yes: "§2Yes"
  no: "§cNo"
```
    
## Screenshots
![image](https://github.com/user-attachments/assets/9dc0b285-f736-4651-a719-6556e2851804)
![image](https://github.com/user-attachments/assets/88ed7d73-7362-469f-a652-cd73a720a3d9)
![image](https://github.com/user-attachments/assets/5cb8e867-35b5-4c8c-9bc2-af68de16104d)

### 📝 License
This project is licensed under MIT license. Please see the [LICENSE](./LICENSE) file for details.
