package de.cloudypanda.main.core.config

class CoreRequestConfig(val enabled: Boolean, val url: String, val apiKey: String) {
    constructor() : this(false, "", "")
}