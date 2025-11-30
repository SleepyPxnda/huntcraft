package de.cloudypanda.core.integrations.discord

import de.cloudypanda.Huntcraft
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class DiscordWebhookMessage {
    private var client: OkHttpClient = OkHttpClient()
    private var username: String
    private var avatarUrl: String
    private var content: String
    private var webhookEnabled = Huntcraft.instance.config.getBoolean("discord.enabled")
    private var webhookUrl = Huntcraft.instance.config.getString("discord.webhookURL")

    constructor(content: String, username: String, avatarUrl: String) {
        this.content = content
        this.username = username
        this.avatarUrl = avatarUrl
    }

    fun send() {
        if (!webhookEnabled || webhookUrl == null) return

        val requestContent = """
            {
                "username": "$username",
                "avatar_url": "$avatarUrl",
                "content": "$content"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(webhookUrl!!)
            .post(requestContent.toRequestBody())
            .header("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            println(response.body!!.string())
        }
    }
}