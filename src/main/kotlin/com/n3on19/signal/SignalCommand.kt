package com.n3on19.signal

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SignalCommand(
    private val plugin: Signal
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : Boolean {
        if (args.isEmpty()) {
            plugin.credits.send(sender)
            return true
        }

        when (args[0].lowercase()) {

            "help" -> {
                sender.sendMessage("§b/signal status")
                sender.sendMessage("§b/signal reload")
                sender.sendMessage("§b/signal pm <true|false>")
                sender.sendMessage("§b/signal mentions <true|false>")
                sender.sendMessage("§b/signal discord <true|false>")
                sender.sendMessage("§b/signal join <true|false>")
                sender.sendMessage("§b/signal leave <true|false>")
                sender.sendMessage("§b/signal effect <effect> <true|false>")
                return true
            }

            "credits", "about" -> {
                plugin.credits.send(sender)
                return true
            }

            "reload" -> {
                plugin.reloadConfig()

                plugin.setMeowMode(
                    plugin.config.getBoolean(
                        "chat.effects.meow-mode",
                        false
                    )
                )

                sender.sendMessage("§aSignal configuration reloaded.")
                return true
            }

            "status" -> {
                val discord = plugin.config.getBoolean("discord.enabled", false)
                val join = plugin.config.getBoolean("chat.join", true)
                val leave = plugin.config.getBoolean("chat.leave", true)
                val pmEnabled = plugin.config.getBoolean("chat.private-message.enabled", true)
                val mentionsEnabled = plugin.config.getBoolean("chat.mentions.enabled", true)
                val cooldownEnabled = plugin.config.getBoolean("chat.cooldown.enabled", true)
                val cooldownSeconds = plugin.config.getDouble("chat.cooldown.seconds", 3.0)
                val meow = plugin.isMeowMode()

                sender.sendMessage("§8§m--------------------")
                sender.sendMessage("§bSignal Status")
                sender.sendMessage("§8§m--------------------")
                sender.sendMessage("§7Discord: ${if (discord) "§aON" else "§cOFF"}")
                sender.sendMessage("§7Join messages: ${if (join) "§aON" else "§cOFF"}")
                sender.sendMessage("§7Leave messages: ${if (leave) "§aON" else "§cOFF"}")
                sender.sendMessage("§7Private messages: ${if (pmEnabled) "§aON" else "§cOFF"}")
                sender.sendMessage("§7Mentions: ${if (mentionsEnabled) "§aON" else "§cOFF"}")
                sender.sendMessage(
                    "§7Chat cooldown: ${if (cooldownEnabled) "§aON" else "§cOFF"} §7[${cooldownSeconds}s]")
                sender.sendMessage("§7Meow Mode: ${if (meow) "§aON" else "§cOFF"}")
                sender.sendMessage("§8§m--------------------")

                return true
            }

            "pm" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /signal pm <true|false>")
                    return true
                }

                val enabled = args[1].toBooleanStrictOrNull()

                if (enabled == null) {
                    sender.sendMessage("§cUsage: /signal pm <true|false>")
                    return true
                }

                plugin.config.set(
                    "chat.private-message.enabled",
                    enabled
                )

                plugin.saveConfig()

                sender.sendMessage(
                    "${if (enabled) "§a" else "§c"}Private messages have been ${
                        if (enabled) "enabled" else "disabled"
                    }."
                )

                return true
            }

            "mentions" -> {
                if (args.size < 2) {
                    sender.sendMessage("Usage: /signal mentions <true|false>")
                    return true
                }

                val mentionsEnabled = args[1].toBooleanStrictOrNull()

                if (mentionsEnabled == null) {
                    sender.sendMessage("§cUsage: /signal mentions <true|false>")
                    return true
                }

                plugin.config.set(
                    "chat.mentions.enabled",
                    mentionsEnabled
                )

                plugin.saveConfig()

                sender.sendMessage(
                    "${if (mentionsEnabled) "§a" else "§c"}Mentions have been ${
                        if (mentionsEnabled) "enabled" else "disabled"
                    }."
                )

                return true
            }

            "cooldown" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /signal cooldown <true|false|seconds>")
                    return true
                }

                val value = args[1]

                when (value.lowercase()) {

                    "true", "false" -> {
                        val enabled = value.toBoolean()

                        plugin.config.set(
                            "chat.cooldown.enabled",
                            enabled
                        )

                        plugin.saveConfig()

                        sender.sendMessage(
                            "${if (enabled) "§a" else "§c"}Private messages have been ${
                                if (enabled) "enabled" else "disabled"
                            }."
                        )
                    }

                    else -> {
                        val seconds = value.toDoubleOrNull()

                        if (seconds == null || seconds < 0) {
                            sender.sendMessage(
                                "§cCooldown must be true, false, or a number of seconds."
                            )
                            return true
                        }

                        plugin.config.set(
                            "chat.cooldown.seconds",
                            seconds
                        )

                        plugin.saveConfig()

                        sender.sendMessage(
                            "§aChat cooldown set to §e${seconds}s§a."
                        )
                    }
                }

                return true
            }

            "discord" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /signal discord <true|false>")
                    return true
                }

                when (args[1].lowercase()) {

                    "true" -> {
                        plugin.config.set("discord.enabled", true)
                        plugin.saveConfig()

                        if (plugin.discord == null) {
                            plugin.discord = DiscordManager(plugin)
                        }
                        plugin.discord?.start()

                        sender.sendMessage("§aDiscord integration enabled.")
                    }

                    "false" -> {
                        plugin.config.set("discord.enabled", false)
                        plugin.saveConfig()

                        plugin.discord?.stop()
                        plugin.discord = null

                        sender.sendMessage("§cDiscord integration disabled.")
                    }

                    else -> {
                        sender.sendMessage("§cValue must be true or false.")
                    }
                }

                return true
            }

            "join" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /signal join <true|false>")
                    return true
                }

                when (args[1].lowercase()) {

                    "true" -> {
                        plugin.config.set("chat.join", true)
                        plugin.saveConfig()

                        sender.sendMessage("§aJoin messages enabled.")
                    }

                    "false" -> {
                        plugin.config.set("chat.join", false)
                        plugin.saveConfig()

                        sender.sendMessage("§cJoin messages disabled.")
                    }

                    else -> {
                        sender.sendMessage("§cValue must be true or false.")
                    }
                }

                return true
            }

            "leave" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /signal leave <true|false>")
                    return true
                }

                when (args[1].lowercase()) {

                    "true" -> {
                        plugin.config.set("chat.leave", true)
                        plugin.saveConfig()

                        sender.sendMessage("§aLeave messages enabled.")
                    }

                    "false" -> {
                        plugin.config.set("chat.leave", false)
                        plugin.saveConfig()

                        sender.sendMessage("§cLeave messages disabled.")
                    }

                    else -> {
                        sender.sendMessage("§cValue must be true or false.")
                    }
                }

                return true
            }

            "effect" -> {
                if (args.size < 3) {
                    sender.sendMessage(
                        "§cUsage: /signal effect <effect> <true|false>"
                    )
                    return true
                }

                when (args[1].lowercase()) {

                    "meow" -> {
                        when (args[2].lowercase()) {

                            "true" -> {
                                plugin.config.set(
                                    "chat.effects.meow-mode",
                                    true
                                )
                                plugin.saveConfig()

                                plugin.setMeowMode(true)

                                sender.sendMessage(
                                    "§aMeow Mode enabled. Meow."
                                )
                            }

                            "false" -> {
                                plugin.config.set(
                                    "chat.effects.meow-mode",
                                    false
                                )
                                plugin.saveConfig()

                                plugin.setMeowMode(false)

                                sender.sendMessage(
                                    "§cMeow Mode disabled."
                                )
                            }

                            else -> {
                                sender.sendMessage(
                                    "§cValue must be true or false."
                                )
                            }
                        }
                    }

                    else -> {
                        sender.sendMessage(
                            "§cUnknown effect: ${args[1]}"
                        )
                    }
                }

                return true
            }

            else -> {
                sender.sendMessage(
                    "§cUnknown setting: ${args[0]}"
                )
                return true
            }
        }
    }
}