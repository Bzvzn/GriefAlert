package com.bzvzn.griefalert;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class AlertListener implements Listener {
    private final ChatNotifier chatNotifier;
    private final DiscordNotifier discordNotifier;
    private final int playtimeThresholdTicks;
    private final Set<Material> monitoredMaterials;

    public AlertListener(ChatNotifier chatNotifier, DiscordNotifier discordNotifier, int playtimeThresholdMinutes, List<String> configMaterials, Logger logger) {
        this.chatNotifier = chatNotifier;
        this.discordNotifier = discordNotifier;
        
        // Minutes to tick (1 second = 20 ticks)
        this.playtimeThresholdTicks = playtimeThresholdMinutes * 1200;
        
        this.monitoredMaterials = EnumSet.noneOf(Material.class);
        
        //Convert list of material to real material data
        for (String matName : configMaterials) {
            try {
                Material material = Material.valueOf(matName.toUpperCase());
                this.monitoredMaterials.add(material);
            } catch (IllegalArgumentException e) {
                logger.warning("Unknow Material found in config.yaml. Skipping it: " + matName);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material placedBlock = event.getBlockPlaced().getType();
        
        if (monitoredMaterials.contains(placedBlock)) {
            checkAndAlert(event.getPlayer(), "placed " + placedBlock.name());
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null) {
                Material usedItem = event.getItem().getType();
                
                if (monitoredMaterials.contains(usedItem)) {
                    checkAndAlert(event.getPlayer(), "uses " + usedItem.name());
                }
            }
        }
    }

    private void checkAndAlert(Player player, String action) {
        int playtimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        
        if (playtimeTicks < playtimeThresholdTicks) {
            // Convert ticks back to readable minutes (1 minute = 1200 ticks)
            int playedMinutes = playtimeTicks / 1200;

            String worldName = player.getWorld().getName();
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            
            // Append the playtime to the action string
            String detailedAction = action + " [Playtime: " + playedMinutes + "m] [Loc: " + worldName + " " + x + " " + y + " " + z + "]";
            
            chatNotifier.send(player, detailedAction);
            discordNotifier.send(player.getName(), detailedAction);
        }
    }
}