package de.cloudypanda.database

import org.jetbrains.exposed.dao.id.UUIDTable

object PlayerSessionTable : UUIDTable("player_sessions") {
    val playerUuid = uuid("player_uuid").references(PlayerTable.uuid)
    val loginTime = long("login_time")
}