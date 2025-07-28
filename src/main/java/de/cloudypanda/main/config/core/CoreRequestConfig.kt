package de.cloudypanda.main.config.core

class CoreRequestConfig(val enabled: Boolean, val url: String, val apiKey: String) {
    constructor() : this(false, "", "")
}