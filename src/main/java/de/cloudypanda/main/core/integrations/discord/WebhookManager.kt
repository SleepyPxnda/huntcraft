package de.cloudypanda.main.core.integrations.discord

import de.cloudypanda.main.Huntcraft
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class WebhookManager {

    companion object {
        private var client: OkHttpClient = OkHttpClient()

        fun sendDeathMessage(deathMessage: String) {
            val coreConfigModel = Huntcraft.instance.coreConfigManager.readFromFile()
            if(!coreConfigModel.webhook.enabled) return

            val requestContent = String.format(
                """
                        {
                            "username": "Death Bot",
                            "avatar_url":"https://www.iconpacks.net/icons/1/free-trash-icon-347-thumb.png",
                            "content": "@here %s"
                        }
                """, deathMessage
            )
            val request = Request.Builder()
                .url(coreConfigModel.webhook.webhookUrl)
                .post(requestContent.toRequestBody())
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                println(response.body!!.string())
            }
        }

        fun sendAchievementMessage(message: String) {
            val coreConfigModel = Huntcraft.instance.coreConfigManager.readFromFile()
            if(!coreConfigModel.webhook.enabled) return

            val requestContent = String.format(
                """
                        {
                            "username": "Achievement Bot",
                            "avatar_url":"https://www.iconpacks.net/free-icon/medal-1369.html",
                            "content": "@here %s"
                        }
                """, message
            )
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
}
