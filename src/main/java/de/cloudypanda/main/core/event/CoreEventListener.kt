package de.cloudypanda.main.core.event

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.util.TextUtil
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


class CoreEventListener() : Listener {
    private val playDuration: MutableMap<UUID, Long> = HashMap<UUID, Long>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        val deathTimeoutConfig = Huntcraft.instance.deathTimerConfigManager.readFromFile()
        val coreConfig = Huntcraft.instance.coreConfigManager.readFromFile()

        e.joinMessage(TextUtil.getJoinIndicator(e.player.name))
        e.player.sendMessage(TextUtil.getJoinMessage(
            coreConfig.infos.rulesLink,
            coreConfig.infos.discordLink,
            coreConfig.infos.websiteLink,
            coreConfig.deathTimer.enabled,
            coreConfig.adventCalendar.enabled,
            deathTimeoutConfig.deathTimeout))

        playDuration[e.player.uniqueId] = System.currentTimeMillis()

        Huntcraft.instance.tablistManager.updateAllPlayerTablist()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {

        val sessionStartMillis = playDuration[e.player.uniqueId] ?: 0

        val sessionDurationMillis = System.currentTimeMillis() - sessionStartMillis

        val sessionDurationString = sessionDurationMillis.milliseconds.toComponents { hours, minutes, seconds, _ ->
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        }

        e.quitMessage(TextUtil.getQuitIndicator(e.player.name, sessionDurationString))
    }

    @EventHandler
    fun onAsyncChatEvent(e: AsyncChatEvent) {
        e.isCancelled = true
        val player = e.player
        val message = e.message()
        e.message(player.displayName().append(Component.text(": ").append(message)))
        e.viewers().forEach { viewer ->
            viewer.sendMessage(player.displayName().append(Component.text(": ").append(message)))
        }
    }
}