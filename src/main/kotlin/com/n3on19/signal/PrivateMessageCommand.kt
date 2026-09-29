package com.n3on19.signal

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PrivateMessageCommand(
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

        if (args.size < 2) {
            sender.sendMessage("§cUsage: /msg <player> <message>")
            return true
        }

        val target = args[0]

        val messageParts = args.copyOfRange(1, args.size)
        val message = messageParts.joinToString(" ")

        plugin.privateMessages?.sendMessage(
            sender,
            target,
            message
        )

        return true
    }
}