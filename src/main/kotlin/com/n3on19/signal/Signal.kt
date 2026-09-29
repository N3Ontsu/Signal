package com.n3on19.signal

import org.bukkit.plugin.java.JavaPlugin

class Signal : JavaPlugin() {
    var discord: DiscordManager? = null
    private var meowMode = false
    var privateMessages: PrivateMessageManager? = null
    lateinit var credits: Credits

    override fun onEnable() {
        saveDefaultConfig()

        server.pluginManager.registerEvents(ChatListener(this), this)
        server.pluginManager.registerEvents(PlayerListener(this), this)
        discord = DiscordManager(this)
        credits = Credits(this)

        meowMode = config.getBoolean(
            "chat.effects.meow-mode", false
        )

        privateMessages = PrivateMessageManager(this)

        if (config.getBoolean("discord.enabled", false)) {
            discord?.start()
        }

        getCommand("signal")?.setExecutor(SignalCommand(this))
        getCommand("msg")?.setExecutor(PrivateMessageCommand(this))
        getCommand("r")?.setExecutor(ReplyCommand(this))
        getCommand("signal")?.tabCompleter = SignalCommandTabCompleter()

        val signalCommand = getCommand("chatter")

        if (signalCommand != null) {
            signalCommand.setExecutor(SignalCommand(this))
            signalCommand.tabCompleter = SignalCommandTabCompleter()
        }

        logger.info("Signal enabled!")
    }

    fun setMeowMode(enabled: Boolean) {
        meowMode = enabled
    }

    fun isMeowMode(): Boolean {
        return meowMode
    }

    override fun onDisable() {
        discord?.stop()
        discord = null

        logger.info("Signal disabled!")
    }
}
