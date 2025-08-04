package de.cloudypanda.huntcraft.application.event

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.huntcraft.application.services.DeathTimerApplicationService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

/**
 * Event listener for death timer related events.
 * Delegates to application services.
 */
class DeathTimerEventListener(
    private val deathTimerApplicationService: DeathTimerApplicationService
) : Listener {

    @EventHandler
    fun onDeathEvent(event: PlayerDeathEvent) {
        val deathMessage = PlainTextComponentSerializer.plainText().serialize(event.deathMessage() ?: Component.text(""))
        deathTimerApplicationService.handlePlayerDeath(event.player, deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(event: PlayerPostRespawnEvent) {
        val (canJoin, message) = deathTimerApplicationService.checkPlayerJoin(
            event.player.uniqueId,
            event.player.name
        )
        
        if (!canJoin && message != null) {
            event.player.kick(Component.text(message))
        }
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        val (canJoin, message) = deathTimerApplicationService.checkPlayerJoin(
            event.uniqueId,
            event.name
        )
        
        if (!canJoin && message != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(message))
        }
    }
}