package com.bzvzn.griefalert;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GatpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Ensure the sender is an actual player, not the server console
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by players.");
            return true;
        }

        if (args.length != 4) {
            player.sendMessage("§cUsage: /gatp <world> <x> <y> <z>");
            return true;
        }

        // Check if the target world exists
        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            player.sendMessage("§cError: The world '" + args[0] + "' does not exist.");
            return true;
        }

        try {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);

            //Cross-dimensional teleportation
            Location targetLoc = new Location(world, x, y, z);
            player.teleport(targetLoc);
            player.sendMessage("§a[📍] Teleported to " + world.getName() + " (" + x + ", " + y + ", " + z + ")");
            
        } catch (NumberFormatException e) {
            player.sendMessage("§cError: Coordinates must be valid numbers.");
        }

        return true;
    }
}