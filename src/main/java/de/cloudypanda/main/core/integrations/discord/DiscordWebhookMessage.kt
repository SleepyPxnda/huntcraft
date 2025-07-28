package de.cloudypanda.main.core.integrations.discord

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class DiscordWebhookMessage {
    private var client: OkHttpClient = OkHttpClient()
    private var username: String
    private var avatarUrl: String
    private var content: String

    constructor(content: String, username: String, avatarUrl: String) {
        this.content = content
        this.username = username
        this.avatarUrl = avatarUrl
    }

    fun send() {
        val coreConfigModel = de.cloudypanda.main.Huntcraft.instance.coreConfigManager.readFromFile()
        if (!coreConfigModel.webhook.enabled) return

        val requestContent = """
            {
                "username": "$username",
                "avatar_url": "$avatarUrl",
                "content": "@here $content"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(coreConfigModel.webhook.webhookUrl)
            .post(requestContent.toRequestBody())
            .header("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            println(response.body!!.string())
        }
    }
}