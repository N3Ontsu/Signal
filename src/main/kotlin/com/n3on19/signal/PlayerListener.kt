package com.n3on19.signal

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(
    private val plugin: Signal
) : Listener {
    private val miniMessage = MiniMessage.miniMessage()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!plugin.config.getBoolean("chat.join-leave-messages.join", true)) {
            return
        }

        val formatJoin = plugin.config.getString(
            "chat.join-leave-messages.join-format"
        ) ?: return

        event.joinMessage(
            miniMessage.deserialize(
                formatJoin,
                Placeholder.unparsed("player", event.player.name)
            )
        )
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        if (!plugin.config.getBoolean("chat.join-leave-messages.leave", true)) {
            return
        }

        val formatLeave = plugin.config.getString(
            "chat.join-leave-messages.leave-format"
        ) ?: return

        event.quitMessage(
            miniMessage.deserialize(
                formatLeave,
                Placeholder.unparsed("player", event.player.name)
            )
        )
    }
}