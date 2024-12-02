package de.cloudypanda.main.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor.color
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class TextUtil {
    companion object {
        fun getJoinIndicator(playerName: String): Component {
            return Component.text("[").color(color(128, 128, 128))
                .append(Component.text("☁").color(color(0, 100, 0)))
                .append(Component.text("] ").color(color(128, 128, 128)))
                .append(Component.text(playerName).color(color(255, 255, 255)))
        }

        fun getJoinMessage(deathTimeout: Long, rulesLink: String, discordLink: String, websiteLink: String, isDeathTimeoutActivated: Boolean, isAdventCalendarActivated: Boolean): Component {
            val formattedDeathTimeout = deathTimeout.milliseconds.toComponents { hours, minutes, seconds, _ ->
                "%02d:%02d:%02d".format(hours, minutes, seconds)
            }

            var text = Component.text("🌟 This Server uses the Huntcraft Plugin 🌟").color(color(128, 128, 128))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("📜 Rules -> ").color(color(128, 128, 128)))
                .append(Component.text(rulesLink).color(color(181, 31, 8)))
                .append(Component.newline())
                .append(Component.text("💬 Discord -> ").color(color(128, 128, 128)))
                .append(Component.text(discordLink).color(color(115, 138, 219)))
                .append(Component.newline())
                .append(Component.text("🌐 Website -> ").color(color(128, 128, 128)))
                .append(Component.text(websiteLink).color(color(102, 255, 51)))
                .append(Component.newline())
                .append(Component.newline())

            if (isDeathTimeoutActivated) {
                text = text.append(Component.text("⏳ Current Deathtimeout: ").color(color(128, 128, 128)))
                    .append(Component.text(formattedDeathTimeout).color(color(255, 153, 0)))
                    .append(Component.newline())
            }

            if (isAdventCalendarActivated) {
                text = text.append(Component.text("🎁 To submit items use ").color(color(128, 128, 128)))
                    .append(
                        Component.text("/submit")
                            .color(color(255, 153, 0))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/huntcraft-submit"))
                    )
                    .append(Component.newline())
                    .append(Component.newline())
            }

            text = text.append(Component.text("🎉 Have fun! 🎉")
                .color(color(128, 128, 128)))
                .append(Component.newline())

            return text
        }

        fun getQuitIndicator(playerName: String, sessionDurationString: String): Component {
            return Component.text("[").color(color(128, 128, 128))
                .append(Component.text("☁").color(color(139, 0, 0)))
                .append(Component.text("] ").color(color(128, 128, 128)))
                .append(Component.text(playerName).color(color(255, 255, 255)))
                .append(Component.text(" (played for $sessionDurationString)"))
        }

        fun getSubmitConfirmationMessage(): Component {
            return Component.newline()
                .append(Component.text("❓ Do you really want to submit the item ❓"))
                .append(Component.newline())
                .append(Component.text("⚠ Continuing WILL destroy the item! ⚠").color(color(255, 0, 0)))
                .append(Component.newline())
                .append(
                    Component.text("[✅ Yes]")
                        .color(color(102, 255, 51))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/huntcraft-submit force"))
                )
        }

        fun getNoChallengeTodayMessage(): Component {
            return Component.text("❌ There is no challenge for today ❌")
                .color(color(255, 0, 0))
        }

        fun getChallengeAlreadyCompletedMessage(): Component {
            return Component.text("✅ You have already completed the challenge for today ✅")
                .color(color(0, 255, 0))
        }

        fun getChallengeCompletedMessage(): Component {
            return Component.text("🎉 You have successfully completed the challenge for today! 🎉")
                .color(color(0, 255, 0))
        }

        fun getChallengeItemNotFoundMessage(): Component {
            return Component.text("🔍 You do not have the required item in your inventory 🔍")
                .color(color(255, 165, 0))
        }

        fun getTablistHeader(): Component {
            return Component.newline()
                .append(Component.text(" ☁ "))
                .append(Component.text("Smoothcloud ").color(color(128, 128, 128)))
                .append(Component.text("X-MAS ").color(color(255, 0, 0)))
                .append(Component.text("Event").color(color(128, 128, 128)))
                .append(Component.newline())
        }

        fun getTablistFooter(challengeMessage: String?, challengePoints: Int?, challengeCompleted: Boolean): TextComponent {
            val challengeStatusText = if (challengeCompleted) "✅ Completed" else "❌ Not completed"
            val challengeStatusColor = if (challengeCompleted) color(0, 255, 0) else color(255, 0, 0)

            return Component.text("\uD83D\uDCC5 Today's challenge \uD83D\uDCC5").color(color(255, 153, 0))
                .append(Component.newline())
                .append(Component.text("($challengePoints Points)").color(color(200, 103, 0)))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text(challengeMessage ?: "❌ No challenge today!").color(color(0, 204, 255)))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Status: ").color(color(255, 255, 255)))
                .append(Component.text(challengeStatusText).color(challengeStatusColor))
        }

        fun getDeathTimerKickMessage(formattedTimeout: String): Component {
            return Component.text("🚫 Due to the rules of 'Huntcraft' 🚫")
                .color(color(255, 0, 0))
                .append(Component.newline())
                .append(Component.text("You were dispelled from the server for:").color(color(255, 255, 255)))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text(formattedTimeout).color(color(255, 0, 0)))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("📜 Read more about the rules in our discord 📜").color(color(115, 138, 219)))
        }

        fun getDeathTimerTimeoutMessage(formattedDate: String, latestDeath: Long, timeout: Long): Component {
            return Component.text("🚫 You died. 🚫").color(color(255, 0, 0))
                .append(Component.newline())
                .append(Component.text("You can't rejoin until ").color(color(255, 255, 255)))
                .append(Component.text(formattedDate).color(color(255, 215, 0)))
                .append(Component.text(".").color(color(255, 255, 255)))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("⏳ Time until rejoin is possible:").color(color(255, 255, 255)))
                .append(Component.newline())
                .append(
                    Component.text(
                        DateUtil.getFormattedDurationUntilJoin(
                            Instant.now().toEpochMilli(),
                            latestDeath,
                            timeout
                        )
                    )
                        .color(color(124, 252, 0))
                )
        }

        fun getPlayerDeathAnnounceMessage(playerName: String, formattedJoinDate: String): Component {
            return Component.text("🔥 $playerName has died and was dispelled from the server until $formattedJoinDate 🔥").color(color(255, 0, 0))
        }
    }
}