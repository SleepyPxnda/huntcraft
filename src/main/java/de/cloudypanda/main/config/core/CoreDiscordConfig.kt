package de.cloudypanda.main.config.core

class CoreDiscordConfig(val enabled: Boolean, val webhookUrl: String) {
    constructor() : this(false, "")
}