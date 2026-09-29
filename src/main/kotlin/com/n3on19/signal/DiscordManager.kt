package com.n3on19.signal

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DiscordManager(
    private val plugin: Signal
) {

    private var jda: JDA? = null

    private val httpClient = HttpClient.newHttpClient()

    fun start() {
        val token = plugin.config.getString("discord.bot.token")

        if (token == null) {
            plugin.logger.warning("Discord bot token is missing!")
            return
        }

        try {
            jda = JDABuilder.createDefault(token)
                .enableIntents(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(DiscordListener(plugin))
                .build()

            plugin.logger.info("Connecting to Discord...")

        } catch (exception: Exception) {
            plugin.logger.severe("Failed to start Discord bot!")
            exception.printStackTrace()
        }
    }

    fun sendMinecraftMessage(playerName: String, message: String) {
        val webhookUrl = plugin.config.getString("discord.webhook.url")

        if (webhookUrl == null) {
            plugin.logger.warning("Discord webhook URL is missing!")
            return
        }

        val json = """
            {
                "username": "$playerName",
                "avatar_url": "https://mc-heads.net/avatar/$playerName",
                "content": "$message"
            }
        """

        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build()

        httpClient.sendAsync(
            request,
            HttpResponse.BodyHandlers.ofString()
        ).thenAccept { response ->

            if (response.statusCode() !in 200..299) {
                plugin.logger.warning(
                    "Discord webhook returned ${response.statusCode()}: ${response.body()}"
                )
            } else {
                plugin.logger.info(
                    "Minecraft message sent to Discord successfully!"
                )
            }

        }.exceptionally { exception ->

            plugin.logger.warning(
                "Failed to send message to Discord: ${exception.message}"
            )

            null
        }
    }

    fun stop() {
        jda?.shutdown()
        jda = null
    }
}