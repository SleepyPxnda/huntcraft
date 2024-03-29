package de.cloudypanda.main.integrations;

import de.cloudypanda.main.Huntcraft;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

public class WebhookManager {

    private static final String webhookUrl = "[WebHook]";

    public static void sendDeathMessage(String deathMessage) {
        String requestContent = String.format(
                """
                        {
                            "username": "Death Bot",
                            "avatar_url":"https://www.iconpacks.net/icons/1/free-trash-icon-347-thumb.png",
                            "content": "@here %s"
                        }
                """, deathMessage);
        try {
            URL url = new URL(webhookUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream stream = connection.getOutputStream();
            stream.write(requestContent.getBytes());
            stream.flush();
            stream.close();

            connection.getInputStream().close(); //I'm not sure why but it doesn't work without getting the InputStream
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
