package com.n3on19.signal

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class SignalCommandTabCompleter : TabCompleter {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {

        if (args.size == 1) {
            return listOf(
                "status",
                "reload",
                "pm",
                "mentions",
                "cooldown",
                "discord",
                "join",
                "leave",
                "effect",
                "credits",
                "about",
                "help"
            )
        }

        if (args.size == 2) {
            when (args[0].lowercase()) {
                "discord", "join", "leave", "pm", "mentions" -> {
                    return listOf("true", "false")
                }

                "cooldown" -> {
                    return listOf("true", "false")
                }

                "effect" -> {
                    return listOf("meow")
                }
            }
        }

        if (args.size == 3) {
            if (
                args[0].lowercase() == "effect" &&
                args[1].lowercase() == "meow"
            ) {
                return listOf("true", "false")
            }
        }

        return emptyList()
    }
}