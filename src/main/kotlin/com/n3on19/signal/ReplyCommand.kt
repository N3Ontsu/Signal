package com.n3on19.signal

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class ReplyCommand(
    private val plugin: Signal
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use private messages.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /r <message>")
            return true
        }

        val message = args.joinToString(" ")

        plugin.privateMessages?.reply(
            sender,
            message
        )

        return true
    }
}