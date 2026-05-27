package com.bzvzn.griefalert;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class AlertListener implements Listener {
    private final ChatNotifier chatNotifier;
    private final DiscordNotifier discordNotifier;
    private final int playtimeThresholdTicks;
    private final Set<Material> monitoredMaterials;

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private final long cooldownTimeMs;

    public AlertListener(ChatNotifier chatNotifier, DiscordNotifier discordNotifier, int playtimeThresholdMinutes, List<String> configMaterials, int cooldownSeconds, Logger logger) {
        this.chatNotifier = chatNotifier;
        this.discordNotifier = discordNotifier;
        
        // Convert minutes to ticks (1 second = 20 ticks, 1 minute = 1200 ticks)
        this.playtimeThresholdTicks = playtimeThresholdMinutes * 1200;

        this.cooldownTimeMs = cooldownSeconds * 1000L;
        
        this.monitoredMaterials = EnumSet.noneOf(Material.class);
        
        // Convert string list from config to actual Material enums
        for (String matName : configMaterials) {
            try {
                Material material = Material.valueOf(matName.toUpperCase());
                this.monitoredMaterials.add(material);
            } catch (IllegalArgumentException e) {
                logger.warning("Unknown Material found in config.yml. Skipping it: " + matName);
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

                if (usedItem.isBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    return; 
                }
                
                if (monitoredMaterials.contains(usedItem)) {
                    checkAndAlert(event.getPlayer(), "used " + usedItem.name());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }


    private void checkAndAlert(Player player, String action) {
        if (player.hasPermission("griefalert.bypass")) {
            return;
        }

        int playtimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        
        if (playtimeTicks < playtimeThresholdTicks) {
            long currentTime = System.currentTimeMillis();

            UUID playerId = player.getUniqueId();

            if (cooldowns.containsKey(playerId)) {
                long lastAlertTime = cooldowns.get(playerId);
                if (currentTime - lastAlertTime < cooldownTimeMs) {
                    return;
                }
            }

            cooldowns.put(playerId, currentTime);


            int playedMinutes = playtimeTicks / 1200;

            String worldName = player.getWorld().getName();
            
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            
            // 1. Format for Discord (Plain Text)
            String discordAction = action + " [Playtime: " + playedMinutes + "m] [Loc: " + worldName + " x:" + x + ", y:" + y + ", z:" + z + "]";
            discordNotifier.send(player.getName(), discordAction);
            
            // 2. Format for In-Game Chat (Pass raw data to build interactive components)
            chatNotifier.sendInteractive(player.getName(), action, playedMinutes, worldName, x, y, z);
        }
    }
}