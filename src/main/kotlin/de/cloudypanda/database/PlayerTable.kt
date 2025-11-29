package de.cloudypanda.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.util.*

object PlayerTable : Table("players") {
    val uuid = uuid("uuid")
    val onlineTime = long("online_time").default(0)
    val latestDeathTime = long("latest_death_time").default(0)
    val canEnterNether = bool("can_enter_nether").default(false)
    val canEnterEnd = bool("can_enter_end").default(false)
    val completedQuests = array<UUID>("completed_quests").default(emptyList())
    val ongoingQuests = array<UUID>("ongoing_quests").default(emptyList())

    override val primaryKey = PrimaryKey(uuid, name = "PK_Player_UUID")

    fun ResultRow.isAllowedToJoin(deathTimer: Long): Boolean {
        val latest = this[latestDeathTime] // reads the value from the row
        return (System.currentTimeMillis() - latest) >= deathTimer
    }
}