package com.bzvzn.griefalert;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GriefAlert extends JavaPlugin {

    @Override
    public void onEnable() {
        // 1. Load or create the configuration
        // Saves the raw config.yml from the jar into the plugins/GriefAlert folder 
        // if it doesn't already exist.
        saveDefaultConfig();

        // 2. Retrieve values from the config
        int playtimeThreshold = getConfig().getInt("playtime-threshold-minutes", 120);
        List<String> monitoredMaterials = getConfig().getStringList("monitored-materials");
        
        String discordUrl = getConfig().getString("discord.webhook-url", "");
        String discordTitle = getConfig().getString("discord.embed-title", "🚨 Grief-Alarm ausgelöst");
        String discordFormat = getConfig().getString("discord.message-format", "**%player%** %action%!");
        String discordColor = getConfig().getString("discord.embed-color", "#FF0000");

        // 3. Assemble the notifiers
        ChatNotifier chatNotifier = new ChatNotifier(getServer());
        DiscordNotifier discordNotifier = new DiscordNotifier(discordUrl, discordTitle, discordFormat, discordColor, getLogger());

        // 4. Initialize the listener and inject the dependencies
        AlertListener listener = new AlertListener(
                chatNotifier,
                discordNotifier,
                playtimeThreshold,
                monitoredMaterials,
                getLogger()
        );

        if (getCommand("gatp") != null) {
            getCommand("gatp").setExecutor(new GatpCommand());
        }

        // 5. Register the listener with the server to start handling events
        getServer().getPluginManager().registerEvents(listener, this);

        // 6. Print success message to the console
        getLogger().info("GriefAlert has been successfully enabled!");
        getLogger().info("Monitored items: " + monitoredMaterials.size());
    }

    @Override
    public void onDisable() {
        // Called when the server stops or the plugin gets reloaded
        getLogger().info("GriefAlert has been disabled.");
    }
}