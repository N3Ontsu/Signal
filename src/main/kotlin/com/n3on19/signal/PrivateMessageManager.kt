package com.n3on19.signal

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class PrivateMessageManager(
    private val plugin: Signal
) {

    private val miniMessage = MiniMessage.miniMessage()

    private val replyTargets = mutableMapOf<UUID, UUID>()

    fun sendMessage(
        sender: Player,
        targetName: String,
        message: String
    ): Boolean {

        if (!plugin.config.getBoolean(
                "chat.private-message.enabled",
                true
            )
        ) {
            sender.sendMessage("§cPrivate messages are disabled.")
            return true
        }

        val target = Bukkit.getPlayerExact(targetName)

        if (target == null) {
            sender.sendMessage("§cThat player is not online.")
            return true
        }

        if (message.isEmpty()) {
            sender.sendMessage("§cYou can't send an empty message.")
            return true
        }

        val format = plugin.config.getString(
            "chat.private-message.pm-format"
        ) ?: return true

        val component = miniMessage.deserialize(
            format,
            Placeholder.unparsed("sender", sender.name),
            Placeholder.unparsed("message", message)
        )

        sender.sendMessage(component)
        target.sendMessage(component)

        replyTargets[sender.uniqueId] = target.uniqueId
        replyTargets[target.uniqueId] = sender.uniqueId

        return true
    }

    fun reply(
        sender: Player,
        message: String
    ): Boolean {

        if (!plugin.config.getBoolean(
                "chat.private-message.enabled",
                true
            )
        ) {
            sender.sendMessage("§cPrivate messages are disabled.")
            return true
        }

        if (message.isEmpty()) {
            sender.sendMessage("§cYou can't send an empty message.")
            return true
        }

        val targetUuid = replyTargets[sender.uniqueId]

        if (targetUuid == null) {
            sender.sendMessage("§cYou have nobody to reply to.")
            return true
        }

        val target = Bukkit.getPlayer(targetUuid)

        if (target == null) {
            sender.sendMessage("§cThat player is no longer online.")
            replyTargets.remove(sender.uniqueId)
            return true
        }

        val format = plugin.config.getString(
            "chat.private-message.pm-format"
        ) ?: return true

        val component = miniMessage.deserialize(
            format,
            Placeholder.unparsed("sender", sender.name),
            Placeholder.unparsed("message", message)
        )

        sender.sendMessage(component)
        target.sendMessage(component)

        replyTargets[sender.uniqueId] = target.uniqueId
        replyTargets[target.uniqueId] = sender.uniqueId

        return true
    }
}