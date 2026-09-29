package com.n3on19.signal

import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit

class DiscordListener(
    private val plugin: Signal
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {

        // Ignore bots
        if (event.author.isBot) {
            return
        }

        // Only listen to the configured channel
        val channelId = plugin.config.getString("discord.channel-id")

        if (channelId == null || event.channel.id != channelId) {
            return
        }

        val message = event.message.contentDisplay

        if (message == "") {
            return
        }

        val username = event.member?.effectiveName ?: event.author.effectiveName
        val format = plugin.config.getString(
            "discord.format"
        ) ?: return

        val component = miniMessage().deserialize(
            format,
            Placeholder.unparsed("player", username),
            Placeholder.unparsed("message", message)
        )

        Bukkit.getServer().sendMessage(component)
    }
}