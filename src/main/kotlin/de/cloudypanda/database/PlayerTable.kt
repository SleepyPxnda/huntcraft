package de.cloudypanda.database

import org.jetbrains.exposed.sql.Table

object PlayerTable : Table("players") {
    val uuid = uuid("uuid")
    val onlineTime = long("online_time").default(0)
    val latestDeathTime = long("latest_death_time").default(0)

    override val primaryKey = PrimaryKey(uuid, name = "PK_Player_UUID")
}