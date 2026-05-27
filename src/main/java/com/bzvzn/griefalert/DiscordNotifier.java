package com.bzvzn.griefalert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class DiscordNotifier {
    private final String webhookUrl;
    private final String title;
    private final String messageFormat;
    private final int color;
    private final Logger logger;
    private final HttpClient httpClient;

    public DiscordNotifier(String webhookUrl, String title, String format, String hexColor, Logger logger) {
        this.webhookUrl = webhookUrl;
        this.title = title;
        this.messageFormat = format;
        this.logger = logger;
        

        this.httpClient = HttpClient.newHttpClient();
        
        int colorInt = 16711680; //fallback
        try {
            if (hexColor != null && !hexColor.isEmpty()) {
                colorInt = Integer.parseInt(hexColor.replace("#", ""), 16);
            }
        } catch (NumberFormatException e) {
            logger.warning("Unvalid format for color: " + hexColor + ". Use Fallback color.");
        }
        this.color = colorInt;
    }

    public void send(String playerName, String action) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        String description = messageFormat
                .replace("%player%", playerName)
                .replace("%action%", action);

        JsonObject json = new JsonObject();
        JsonArray embeds = new JsonArray();
        JsonObject embed = new JsonObject();
        
        embed.addProperty("title", title);
        embed.addProperty("description", description);
        embed.addProperty("color", color);
        
        embeds.add(embed);
        json.add("embeds", embeds);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .exceptionally(e -> {
                    logger.warning("Could not send Discord Message: " + e.getMessage());
                    return null;
                });
    }
}