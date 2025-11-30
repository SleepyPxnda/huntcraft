package de.cloudypanda.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.event.HoverEvent.showText
import net.kyori.adventure.text.format.TextColor.color
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class TextUtil {
    companion object {
        fun getJoinIndicator(playerName: String): Component {
            return Component.text()
                .append(Component.text("[", color(110, 110, 110)))
                .append(Component.text("☁", color(30, 200, 170)))
                .append(Component.text("] ", color(110, 110, 110)))
                .append(Component.text(playerName, color(245, 245, 245)))
                .build()
        }

        fun getJoinMessage(
            deathTimeout: Int
        ): Component {
            val formattedDeathTimeout = deathTimeout.seconds.toComponents { hours, minutes, seconds, _ ->
                "%02d:%02d:%02d".format(hours, minutes, seconds)
            }

            return Component.text()
                .append(Component.text("═════════════════════════════════", color(90, 90, 90)))
                .append(Component.text("  🌟 Welcome to Huntcraft Season 3 🌟  ", color(200, 200, 200)))
                .append(Component.text("═════════════════════════════════", color(90, 90, 90)))
                .appendNewline()
                .append(Component.text("⏳ Deathtimeout: ", color(170, 170, 170)))
                .append(Component.text(formattedDeathTimeout, color(255, 170, 0)))
                .appendNewline()
                .append(Component.text("🎉 Have fun and play fair! 🎉", color(200, 200, 200)))
                .build()
        }

        fun getQuitIndicator(playerName: String, sessionDurationString: String): Component {
            return Component.text()
                .append(Component.text("[", color(110, 110, 110)))
                .append(Component.text("☁", color(220, 80, 80)))
                .append(Component.text("] ", color(110, 110, 110)))
                .append(Component.text(playerName, color(245, 245, 245)))
                .append(Component.text("  (played for "))
                .append(Component.text(sessionDurationString, color(180, 180, 180)))
                .append(Component.text(")"))
                .build()
        }

        fun getQuestProgressBar(questName: String, progress: Int, maximum: Int): Component {
            val percent = if (maximum > 0) (progress * 100) / maximum else 0
            val filledBars = if (maximum > 0) (progress * 10) / maximum else 0
            val emptyBars = 10 - filledBars

            val filledSegment = "█".repeat(filledBars)
            val emptySegment = "░".repeat(emptyBars)
            val percentText = " $percent%"

            return Component.text()
                .append(Component.text(questName, color(255, 230, 120)))
                .append(Component.text(" | ", color(120, 120, 120)))
                .append(Component.text(filledSegment, color(60, 200, 80)))
                .append(Component.text(emptySegment, color(180, 60, 60)))
                .append(Component.text(percentText, color(150, 220, 255)))
                .build()
        }

        fun getQuestCompletionMessage(questName: String): Component {
            return Component.text("🎉 Quest completed: $questName")
                .color(color(110, 255, 140))
        }

        fun getQuestAfterCompletionText(afterCompletionText: String): Component {
            return Component.text(afterCompletionText)
                .color(color(150, 215, 255))
        }

        fun getPlayerDeathAnnounceMessage(playerName: String, formattedJoinDate: String): Component {
            return Component.text("🔥 $playerName was dispelled until $formattedJoinDate 🔥")
                .color(color(255, 90, 90))
        }

        fun getDeathTimerKickMessage(formattedTimeout: String): Component {
            return Component.text()
                .append(Component.text("🚫 Huntcraft Enforcement 🚫", color(220, 100, 100)))
                .appendNewline()
                .append(Component.text("You have been temporarily expelled from the server.", color(245, 245, 245)))
                .appendNewline()
                .append(Component.text("Return available at: ", color(220, 100, 100)))
                .append(Component.text(formattedTimeout, color(255, 180, 80)))
                .appendNewline()
                .appendNewline()
                .append(Component.text("📜 See the rules on Discord for details.", color(135, 160, 255)))
                .build()
        }

        fun getDeathTimerTimeoutMessage(formattedDate: String, latestDeath: Long, timeout: Long): Component {
            return Component.text()
                .append(Component.text("🚫 You recently died.", color(255, 100, 100)))
                .appendNewline()
                .append(Component.text("Rejoin allowed at: ", color(245, 245, 245)))
                .append(Component.text(formattedDate, color(255, 215, 0)))
                .appendNewline()
                .appendNewline()
                .append(Component.text("⏳ Time remaining:", color(245, 245, 245)))
                .appendNewline()
                .append(
                    Component.text(
                        DateUtil.getFormattedDurationUntilJoin(
                            Instant.now().toEpochMilli(),
                            latestDeath,
                            timeout
                        ), color(180, 255, 140)
                    )
                )
                .build()
        }

        fun getOngoingQuestListCommandMessage(questName: String, description: String, progression: Int, needed: Int): Component {
            val questComponent = Component.text(questName, color(245, 245, 245))
                .hoverEvent(
                    showText(Component.text(description, color(210, 210, 210))) as HoverEvent<Any>
                )

            return Component.text()
                .append(Component.text("⌛", color(255, 215, 0)))
                .append(questComponent)
                .append(Component.text("  "))
                .append(Component.text("($progression/$needed)", color(160, 220, 255)))
                .build()
        }

        fun getCompletedQuestListCommandMessage(questName: String, description: String): Component {
            val questComponent = Component.text(questName, color(120, 120, 120))
                .hoverEvent(
                    showText(Component.text(description, color(210, 210, 210))) as HoverEvent<Any>
                )

            return Component.text()
                .append(Component.text("✅", color(180, 255, 140)))
                .append(questComponent)
                .build()
        }
    }
}