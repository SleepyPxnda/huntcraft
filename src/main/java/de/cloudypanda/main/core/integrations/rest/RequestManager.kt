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
    private val baseUrl: String = Huntcraft.coreConfigModel.requestConfig.url

    fun createPlayer(playerID: UUID, playerName: String): Boolean {
        val createPlayerDto = CreatePlayerDto(playerID, playerName)
        val response = sendPostRequest("$baseUrl/api/players", createPlayerDto.toString())

        return response.statusCode() == 200
    }

    fun updatePlayerChallenge(playerID: UUID, points: Int) {
        val updatePlayerChallengeDto = UpdatePlayerChallengeDto(playerID, points)

        val response = sendPutRequest("$baseUrl/api/player/challenge", updatePlayerChallengeDto.toString())

        if (response.statusCode() == 200) {
            println("Updated $playerID with $points points")
        } else {
            println("Failed to update $playerID with $points points cause of ${response.body()}")
        }
    }

    fun updatePlayerDeath(playerID: UUID, deathTimestamp: Long): Boolean {
        val updatePlayerDeathDto = UpdatePlayerDeathDto(playerID, deathTimestamp)
        val response = sendPutRequest("$baseUrl/api/player/challenge", updatePlayerDeathDto.toString())
        return response.statusCode() == 200
    }

    private fun sendPostRequest(endpoint: String, data: String): HttpResponse<String> {
        val client: HttpClient = HttpClient.newHttpClient()

        val response = HttpRequest.newBuilder()
            .uri(URI(endpoint))
            .POST(HttpRequest.BodyPublishers.ofString(data))
            .build()
            .let { client.send(it, HttpResponse.BodyHandlers.ofString()) }

        return response;
    }

    private fun sendPutRequest(endpoint: String,data: String): HttpResponse<String> {
        val client: HttpClient = HttpClient.newHttpClient()

        val response = HttpRequest.newBuilder()
            .uri(URI(endpoint))
            .PUT(HttpRequest.BodyPublishers.ofString(data))
            .build()
            .let { client.send(it, HttpResponse.BodyHandlers.ofString()) }

        return response;
    }
}
