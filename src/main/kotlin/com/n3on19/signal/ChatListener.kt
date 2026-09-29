package com.n3on19.signal

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.Registry
import org.bukkit.NamespacedKey
import java.util.UUID

class ChatListener(
    private val plugin: Signal
) : Listener {

    private val miniMessage = MiniMessage.miniMessage()
    private val chatCooldowns = mutableMapOf<UUID, Long>()
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val format = plugin.config.getString(
            "chat.format"
        ) ?: return

        val player = event.player

        if (plugin.config.getBoolean("chat.cooldown.enabled", true)) {
            val cooldownSeconds = plugin.config.getDouble(
                "chat.cooldown.seconds",
                3.0
            )

            val now = System.nanoTime()
            val cooldownNanos = (cooldownSeconds * 1_000_000_000).toLong()

            val lastMessage = chatCooldowns[player.uniqueId]

            if (lastMessage != null) {
                val elapsed = now - lastMessage

                if (elapsed < cooldownNanos) {
                    val remaining = (cooldownNanos - elapsed) / 1_000_000_000.0

                    player.sendMessage(
                        "§cYou must wait %.1f seconds before chatting again."
                            .format(remaining)
                    )

                    event.isCancelled = true
                    return
                }
            }

            chatCooldowns[player.uniqueId] = now
        }

        val originalMessage = PlainTextComponentSerializer.plainText()
            .serialize(event.message())

        val finalMessage: String

        if (plugin.config.getBoolean("chat.effects.meow-mode", false)) {
            val tokenizer = java.util.StringTokenizer(originalMessage)
            val builder = StringBuilder()

            while (tokenizer.hasMoreTokens()) {
                tokenizer.nextToken()

                if (builder.isNotEmpty()) {
                    builder.append(" ")
                }

                builder.append("Meow")
            }

            finalMessage = builder.toString()
        } else {
            finalMessage = originalMessage
        }

        // Handle mentions
        if (plugin.config.getBoolean("chat.mentions.enabled", true)) {
            val mentionRegex = Regex("""@([A-Za-z0-9_]{1,16})""")

            mentionRegex.findAll(finalMessage).forEach { match ->
                val mentionedName = match.groupValues[1]

                val mentionedPlayer = Bukkit.getOnlinePlayers()
                    .firstOrNull {
                        it.name.equals(mentionedName, ignoreCase = true)
                    }

                if (
                    mentionedPlayer != null &&
                    plugin.config.getBoolean("chat.mentions.sound", true)
                ) {
                    mentionedPlayer.playSound(
                        mentionedPlayer.location,
                        getMentionSound(),
                        plugin.config.getDouble(
                            "chat.mentions.volume",
                            1.0
                        ).toFloat(),
                        plugin.config.getDouble(
                            "chat.mentions.pitch",
                            1.5
                        ).toFloat()
                    )
                }
            }
        }

        event.renderer { _, _, _, _ ->
            val messageComponent = createMessageComponent(finalMessage)

            miniMessage.deserialize(
                format,
                Placeholder.unparsed("player", player.name),
                Placeholder.component("message", messageComponent)
            )
        }

        if (plugin.config.getBoolean("discord.enabled", false)) {
            plugin.discord?.sendMinecraftMessage(
                player.name,
                finalMessage
            )
        }
    }

    private fun createMessageComponent(message: String): Component {
        if (!plugin.config.getBoolean("chat.mentions.enabled", true)) {
            return Component.text(message)
        }

        val mentionRegex = Regex("""(@[A-Za-z0-9_]{1,16})""")
        val builder = Component.text()
        var lastIndex = 0

        mentionRegex.findAll(message).forEach { match ->
            builder.append(
                Component.text(
                    message.substring(lastIndex, match.range.first)
                )
            )

            val mentionedName = match.groupValues[1].substring(1)

            val mentionedPlayer = Bukkit.getOnlinePlayers()
                .firstOrNull {
                    it.name.equals(mentionedName, ignoreCase = true)
                }

            if (mentionedPlayer != null) {
                builder.append(
                    Component.text(match.value)
                        .color(NamedTextColor.LIGHT_PURPLE)
                        .decorate(TextDecoration.BOLD)
                )
            } else {
                builder.append(
                    Component.text(match.value)
                )
            }

            lastIndex = match.range.last + 1
        }

        builder.append(
            Component.text(message.substring(lastIndex))
        )

        return builder.build()
    }

    private fun getMentionSound(): Sound {
        val soundName = plugin.config.getString(
            "chat.mentions.sound-type",
            "entity_experience_orb_pickup"
        ) ?: "entity_experience_orb_pickup"

        val key = NamespacedKey.minecraft(soundName.lowercase())

        return Registry.SOUNDS.get(key)
            ?: Sound.ENTITY_EXPERIENCE_ORB_PICKUP
    }
}