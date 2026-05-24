package com.bzvzn.griefalert;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChatNotifier {
    private final Server server;

    public ChatNotifier(Server server) {
        this.server = server;
    }

    public void sendInteractive(String playerName, String action, int playtime, String worldKey, int x, int y, int z) {
        
        String tpCommand = "/execute in " + worldKey + " run tp @s " + x + " " + y + " " + z;

        // Clean up the dimension name for display (e.g., "minecraft:overworld" -> "overworld")
        String readableWorld = worldKey.replace("minecraft:", "");

        // Build the clickable coordinates component
        Component coordinates = Component.text("[" + readableWorld + " " + x + " " + y + " " + z + "]", NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand(tpCommand));

        // Assemble the complete interactive message
        Component message = Component.text("[GriefAlert] ", NamedTextColor.RED)
                .append(Component.text(playerName + " ", NamedTextColor.YELLOW))
                .append(Component.text(action, NamedTextColor.GRAY))
                .append(Component.text(" [" + playtime + "m] ", NamedTextColor.DARK_GRAY))
                .append(coordinates);

        // Broadcast to OPs and players with the correct permission
        for (Player onlinePlayer : server.getOnlinePlayers()) {
            if (onlinePlayer.isOp() || onlinePlayer.hasPermission("griefalert.notify")) {
                onlinePlayer.sendMessage(message);
            }
        }
        
        // Log to console (Console will see the text but cannot click it)
        server.getConsoleSender().sendMessage(message);
    }
}