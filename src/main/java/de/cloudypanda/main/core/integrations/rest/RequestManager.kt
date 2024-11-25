package de.cloudypanda.main.core.integrations.rest;

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.core.integrations.rest.models.CreatePlayerDto
import de.cloudypanda.main.core.integrations.rest.models.UpdatePlayerChallengeDto
import de.cloudypanda.main.core.integrations.rest.models.UpdatePlayerDeathDto
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

class RequestManager() {
    private val baseUrl: String = Huntcraft.instance.coreConfigModel.requestConfig.url
    private val isRequestEnabled: Boolean = Huntcraft.instance.coreConfigModel.requestConfig.enabled

    fun createPlayer(playerID: UUID, playerName: String) {
        val createPlayerDto = CreatePlayerDto(playerID, playerName)
        val response = sendPostRequest("$baseUrl/api/players", createPlayerDto.toString()) ?: return

        if (response.statusCode() == 200) {
            println("Created $playerID with name $playerName points")
        } else {
            println("Failed to create $playerID with name $playerName cause of ${response.body()}")
        }
    }

    fun updatePlayerChallenge(playerID: UUID, points: Int) {
        val updatePlayerChallengeDto = UpdatePlayerChallengeDto(playerID, points)

        val response = sendPutRequest("$baseUrl/api/player/challenge", updatePlayerChallengeDto.toString()) ?: return

        if (response.statusCode() == 200) {
            println("Updated $playerID with $points points")
        } else {
            println("Failed to update $playerID with $points points cause of ${response.body()}")
        }
    }

    fun updatePlayerDeath(playerID: UUID, deathTimestamp: Long) {
        val updatePlayerDeathDto = UpdatePlayerDeathDto(playerID, deathTimestamp)
        val response = sendPutRequest("$baseUrl/api/player/challenge", updatePlayerDeathDto.toString()) ?: return

        if (response.statusCode() == 200) {
            println("Updated $playerID with $deathTimestamp")
        } else {
            println("Failed to update $playerID with $deathTimestamp points cause of ${response.body()}")
        }
    }

    private fun sendPostRequest(endpoint: String, data: String): HttpResponse<String>? {
        if(!isRequestEnabled){
            Huntcraft.instance.componentLogger.info("RequestManager is disabled")
            return null;
        }

        val client: HttpClient = HttpClient.newHttpClient()

        val response = HttpRequest.newBuilder()
            .uri(URI(endpoint))
            .POST(HttpRequest.BodyPublishers.ofString(data))
            .build()
            .let { client.send(it, HttpResponse.BodyHandlers.ofString()) }

        return response;
    }

    private fun sendPutRequest(endpoint: String,data: String): HttpResponse<String>? {
        if(!isRequestEnabled){
            Huntcraft.instance.componentLogger.info("RequestManager is disabled")
            return null;
        }

        val client: HttpClient = HttpClient.newHttpClient()

        val response = HttpRequest.newBuilder()
            .uri(URI(endpoint))
            .PUT(HttpRequest.BodyPublishers.ofString(data))
            .build()
            .let { client.send(it, HttpResponse.BodyHandlers.ofString()) }

        return response;
    }
}
