package de.cloudypanda.main.core.integrations.discord;

import de.cloudypanda.main.Huntcraft
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WebhookManager {

    companion object {
        private val webhookUrl: String = "[WebHook]";

        fun sendDeathMessage(deathMessage: String) {
            if(!Huntcraft.instance.coreConfigModel.webhook.enabled) return;

            val requestContent = String.format(
                """
                        {
                            "username": "Death Bot",
                            "avatar_url":"https://www.iconpacks.net/icons/1/free-trash-icon-347-thumb.png",
                            "content": "@here %s"
                        }
                """, deathMessage
            );
            try {
                val url = URL(webhookUrl);
                val connection = url.openConnection() as HttpsURLConnection;
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                val stream = connection.outputStream;
                stream.write(requestContent.encodeToByteArray());
                stream.flush();
                stream.close();

                connection.inputStream.close(); //I'm not sure why but it doesn't work without getting the InputStream
                connection.disconnect();
            } catch (e: IOException) {
                throw RuntimeException(e);
            }
        }

        fun sendAchievementMessage(message: String) {
            if(!Huntcraft.instance.coreConfigModel.webhook.enabled) return;

            val requestContent = String.format(
                """
                        {
                            "username": "Achievement Bot",
                            "avatar_url":"https://www.iconpacks.net/free-icon/medal-1369.html",
                            "content": "@here %s"
                        }
                """, message
            );
            try {
                val url = URL(webhookUrl);
                val connection = url.openConnection() as HttpsURLConnection;
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                val stream = connection.outputStream;
                stream.write(requestContent.encodeToByteArray());
                stream.flush();
                stream.close();

                connection.inputStream.close(); //I'm not sure why but it doesn't work without getting the InputStream
                connection.disconnect();
            } catch (e: IOException) {
                throw RuntimeException(e);
            }
        }
    }
}
