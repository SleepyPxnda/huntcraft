package de.cloudypanda.main.core.integrations.rest;

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.core.integrations.rest.models.CreatePlayerDto
import de.cloudypanda.main.core.integrations.rest.models.UpdatePlayerChallengeDto
import de.cloudypanda.main.core.integrations.rest.models.UpdatePlayerDeathDto
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class RequestManager(val huntcraft: Huntcraft) {

    private val baseUrl: String = huntcraft.coreConfigManager.readFromFile().huntCraftBackendApiKey

    fun createPlayer(createPlayerDto: CreatePlayerDto): Boolean {
        val response = sendPostRequest("$baseUrl/api/players", createPlayerDto.toString())

        return response.statusCode() == 200
    }

    fun updatePlayerChallenge(updatePlayerChallengeDto: UpdatePlayerChallengeDto): Boolean {
        val response = sendPutRequest("$baseUrl/api/player/challenge", updatePlayerChallengeDto.toString())
        return response.statusCode() == 200
    }

    fun updatePlayerDeath(updatePlayerDeathDto: UpdatePlayerDeathDto): Boolean {
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
