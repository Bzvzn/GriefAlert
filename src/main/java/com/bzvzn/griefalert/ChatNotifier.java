package com.bzvzn.griefalert;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ChatNotifier {
    private final Server server;

    public ChatNotifier(Server server) {
        this.server = server;
    }

    public void sendInteractive(String playerName, String action, int playtime, String worldName, int x, int y, int z) {

        String tpCommand = "/gatp " + worldName + " " + x + " " + y + " " + z;

        Component coordinates = Component.text("[" + worldName + " " + x + " " + y + " " + z + "]", NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(Component.text("Click to teleport instantly!", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand(tpCommand));

        Component message = Component.text("[GriefAlert] ", NamedTextColor.RED)
                .append(Component.text(playerName + " ", NamedTextColor.YELLOW))
                .append(Component.text(action, NamedTextColor.GRAY))
                .append(Component.text(" [" + playtime + "m] ", NamedTextColor.DARK_GRAY))
                .append(coordinates);

        for (Player onlinePlayer : server.getOnlinePlayers()) {
            if (onlinePlayer.isOp() || onlinePlayer.hasPermission("griefalert.notify")) {
                onlinePlayer.sendMessage(message);
            }
        }
        
        server.getConsoleSender().sendMessage(message);
    }
}