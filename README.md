# Wanted Plugin for Minecraft

## 🚔 About
**Wanted** is a custom Minecraft plugin that introduces a GTA-style wanted system. Players gain wanted levels (up to 6 stars ⭐) based on their actions, and police must chase them down. The system automatically decreases the wanted level if no police are nearby.

## ✨ Features
- 🔹 **GTA-style wanted system** with up to 5 stars
- 🔹 **Automatic wanted level decay** when no police are nearby (within 10 blocks)
- 🔹 **Flashing star animation** for immersive gameplay
- 🔹 **Configurable settings** to adjust police detection range, decay time, and more

## 📥 Installation
1. Download the latest release.
2. Place the `.jar` file into your `plugins` folder.
3. Restart your Minecraft server.

## ⚙️ Configuration
Modify the `config.yml` file to customize the plugin settings:
```yaml
wanted:
  max_stars: 6
  decay_radius: 10 # Blocks away from police for wanted level to decrease
  decay_time: 30 # Seconds before one star is removed
  flashing_animation: true
```

## 🎮 Commands & Permissions
### Commands
| Command | Description |
|---------|-------------|
| `/wanted` | Displays the current wanted level |
| `/wanted top` | Open Gui Menu For Wanteds |
| `/wanted set <player>` | Set a Player Wanted |
| `/wanted add <player>` | Add Wanted To a Player |
| `/wanted clear <player>` | Clears a player's wanted level |

### Permissions
| Permission | Description |
|------------|-------------|
| `ewanted.use` | Allows players to view their wanted level |
| `ewanted.admin` | Allows admins to clear wanted levels |

## 📢 Support & Contributions
Feel free to report issues or suggest features in the [Issues](https://discord.gg/dJ8exMjuKe) section. Contributions are welcome!

---
⭐ **Stay out of trouble... or don't!** 😈

