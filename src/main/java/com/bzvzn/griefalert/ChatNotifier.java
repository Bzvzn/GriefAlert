package com.bzvzn.griefalert;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ChatNotifier {
    private final Server server;

    public ChatNotifier(Server server) {
        this.server = server;
    }

    public void send(Player player, String action) {
        String message = "§c[GriefAlert] §e" + player.getName() + " §7" + action + "!";
        
        for (Player onlinePlayer : server.getOnlinePlayers()) {
            if (onlinePlayer.isOp() || onlinePlayer.hasPermission("griefalert.notify")) {
                onlinePlayer.sendMessage(message);
            }
        }
        
        server.getConsoleSender().sendMessage(message);
    }
}