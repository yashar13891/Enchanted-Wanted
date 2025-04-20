# 🚨 EnchantedWanted – GTA-Like Wanted System for Minecraft

**EnchantedWanted** brings the thrill of a GTA-style star-based wanted system to your Minecraft world. Whether you're running a roleplay server or just want some chaos, this plugin adds a whole new level of intensity and immersion.

> ⚠️ **This is a beta release!**  
> Please report any bugs, glitches, or suggestions in our [Discord server](https://discord.gg/dJ8exMjuKe). Your feedback helps shape the final version!

---

## ✨ Key Features

- ⭐ **Up to 6 Wanted Stars**  
  Inspired by the GTA series. Each star level increases the “threat level” with glowing animations.

- ⚙️ **Clean & Simple Configuration**  
  Easy to read and customize. No unnecessary clutter.

- 🖼️ **Built-in GUI System**  
  A polished, player-friendly GUI shows current wanted levels and useful info. Layouts and titles are customizable.

- 💾 **Flexible Data Storage**  
  - SQLite (default)  
  - MySQL (for large networks)  
  Optimized to handle 500+ players on MySQL with no performance drop.

- ⚡ **High Performance**  
  Asynchronous tasks and lightweight code ensure lag-free performance.

- 🔌 **Plug & Play Setup**  
  Drop the plugin into your `plugins/` folder and start the server. Default config included. Setup takes under 2 minutes.

- 🧩 **PlaceholderAPI Support**  
  Display wanted levels anywhere using:
  - `%ew_wanted%`
  - `%ew_formatted_wanted%`

---

## ✅ Compatibility

- Supports **Minecraft 1.12 – 1.21**  
- Tested on **Spigot**, **Paper**, and **Purpur**

---

## 🚧 Coming Soon

- 🔗 Police plugin integrations  
- 🎯 Bounty system addon  
- 🌐 Web-based wanted dashboard (beta)

---

## 🧪 Beta Notice

While fully usable and stable, this plugin is still in active beta. We recommend:

- Joining our [Discord](https://discord.gg/dJ8exMjuKe) to share feedback  
- Reporting bugs or unusual behavior  
- Suggesting features you'd love to see

---

## 📜 Commands & Permissions

> All commands use `/wanted` as the base.

| Command                      | Permission                        | Description                            |
|-----------------------------|------------------------------------|----------------------------------------|
| `/wanted top`               | `enchantedwanted.top`             | View top wanted players                |
| `/wanted clear <player>`    | `enchantedwanted.clear`           | Clear a player’s wanted level          |
| `/wanted set <player> <n>`  | `enchantedwanted.set`             | Set a player’s wanted level            |
| `/wanted add <player> <n>`  | `enchantedwanted.add`             | Add wanted stars to a player           |
| `/wanted find <player>`     | `enchantedwanted.find`            | Locate a wanted player                 |
| `/wanted gps <player>`      | `enchantedwanted.gps`             | Start GPS tracking for a player        |
| `/wanted gpsstop`           | `enchantedwanted.gps`             | Stop GPS tracking                      |
| `/ew remove <player> <n>`   | `enchantedwanted.remove`          | Remove stars from a player             |
| `/wanted reload`            | `enchantedwanted.command.reload`  | Reload plugin configs                  |
| `/wanted policealert`       | `enchantedwanted.policealert`     | Alert nearby police of a situation     |

---

## 🙋 Need Help?

We're here to help! Reach out anytime on our [Discord server](https://discord.gg/dJ8exMjuKe).  
Check out plugin stats on **[bStats](https://bstats.org/plugin/bukkit/Enchanted%20Wanted/24710)**.

---

### 🎮 Download EnchantedWanted today and bring law and chaos to your Minecraft server!
