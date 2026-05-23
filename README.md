# 🚨 GriefAlert

GriefAlert is a lightweight and asynchronous Paper/Purpur plugin for Minecraft. It acts as an early warning system for server administrators by immediately sending an alert when new players use potentially dangerous items (like TNT or flint and steel).

## ✨ Features

* **In-game Warnings:** Immediately notifies all OPs on the server via chat. (Or players with permission "griefalert.notify")
* **Discord Integration:** Sends formatted warnings via a Discord webhook (completely asynchronous to avoid blocking the server's main thread).
* **Playtime Threshold:** Griefers usually lack patience. Players who exceed a configured playtime (e.g., 2 hours) are considered trusted and will no longer trigger alarms.
* **Precise Tracking:** The alert automatically includes the exact coordinates and world of the incident.

## 📥 Installation

1. Download the latest `GriefAlert.jar`.
2. Place the file into your server's `plugins/` folder.
3. Restart the server.
4. Navigate to `plugins/GriefAlert/config.yml` and insert your Discord webhook URL.

## ⚙️ Configuration

The `config.yml` is automatically generated on the first startup:

```yaml
# After how many minutes of playtime should a player stop being monitored?
playtime-threshold-minutes: 120

# Which items should trigger an alert?
monitored-materials:
  - TNT
  - FLINT_AND_STEEL
  - LAVA_BUCKET
  - TNT_MINECART
  - END_CRYSTAL
  - RESPAWN_ANCHOR

# Discord Webhook Settings
discord:
  webhook-url: "YOUR_WEBHOOK_URL_HERE"
  embed-title: "🚨 Grief Alert Triggered"
  embed-color: "#FF0000"
```


## 🛠️ For Developers (Compiling)
This project uses Gradle. To compile the plugin yourself:

1. Clone the repository.
2. Run ./gradlew build in the root directory.
3. The compiled .jar will be located in build/libs/.