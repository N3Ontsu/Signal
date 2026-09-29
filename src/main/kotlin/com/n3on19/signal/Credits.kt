package com.n3on19.signal

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

class Credits(
    private val plugin: Signal
) {

    private val miniMessage = MiniMessage.miniMessage()

    fun send(sender: CommandSender) {
        val config = plugin.config

        val title = config.getString(
            "plugin.credits.title",
            "Signal"
        ) ?: "Signal"

        val description = config.getString(
            "plugin.credits.description",
            "A communication & moderation plugin."
        ) ?: "A communication & moderation plugin."

        val developers = config.getStringList(
            "plugin.credits.developers"
        )

        val contributors = config.getStringList(
            "plugin.credits.contributors"
        )

        val libraries = config.getStringList(
            "plugin.credits.libraries"
        )

        sender.sendMessage(
            miniMessage.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            )
        )

        sender.sendMessage(
            miniMessage.deserialize(
                "<aqua><bold>$title</bold></aqua>"
            )
        )

        sender.sendMessage(
            miniMessage.deserialize(
                "<gray>$description"
            )
        )

        sender.sendMessage(
            miniMessage.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            )
        )

        if (developers.isNotEmpty()) {
            sender.sendMessage(
                miniMessage.deserialize(
                    "<gray>Created by"
                )
            )

            developers.forEach {
                sender.sendMessage(
                    miniMessage.deserialize(
                        "  <aqua>$it</aqua>"
                    )
                )
            }
        }

        if (contributors.isNotEmpty()) {
            sender.sendMessage(
                miniMessage.deserialize(
                    "<gray>Contributors"
                )
            )

            contributors.forEach {
                sender.sendMessage(
                    miniMessage.deserialize(
                        "  <light_purple>$it</light_purple>"
                    )
                )
            }
        }

        sender.sendMessage(
            miniMessage.deserialize(
                "<gray>GitHub: <click:open_url:'https://github.com/N3Ontsu/Signal'><aqua><underlined>github.com/N3Ontsu/Signal</underlined></aqua></click>"
            )
        )

        if (libraries.isNotEmpty()) {
            sender.sendMessage(
                miniMessage.deserialize(
                    "<gray>Powered by"
                )
            )

            sender.sendMessage(
                miniMessage.deserialize(
                    "  <yellow>${libraries.joinToString(" • ")}</yellow>"
                )
            )
        }

        val version = plugin.pluginMeta.version

        sender.sendMessage(
            miniMessage.deserialize(
                "<gray>Version: <white>$version</white>"
            )
        )

        sender.sendMessage(
            miniMessage.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            )
        )
    }
}