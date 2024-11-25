package de.cloudypanda.main.core.config

class CoreDiscordConfig(val enabled: Boolean, val webhookUrl: String) {
    constructor() : this(false, "")
}