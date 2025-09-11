package de.cloudypanda.main.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor.color
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class TextUtil {
    companion object {
        fun getJoinIndicator(playerName: String): Component {
            return ComponentBuilder()
                .append("[", color(128, 128, 128))
                .append("☁", color(0, 100, 0))
                .append("] ", color(128, 128, 128))
                .append(playerName, color(255, 255, 255))
                .build()
        }

        fun getJoinMessage(
            rulesLink: String,
            discordLink: String,
            websiteLink: String,
            deathTimeout: Int
        ): Component {
            return ComponentBuilder()
                .append("🌟 This Server uses the Huntcraft Plugin 🌟", color(128, 128, 128))
                .newLine(2)
                .append("📜 Rules -> ", color(128, 128, 128))
                .append(rulesLink, color(181, 31, 8))
                .newLine()
                .append("💬 Discord -> ", color(128, 128, 128))
                .append(discordLink, color(115, 138, 219))
                .newLine()
                .append("🌐 Website -> ", color(128, 128, 128))
                .append(websiteLink, color(102, 255, 51))
                .newLine(2)
                .apply {
                    val formattedDeathTimeout = deathTimeout.seconds.toComponents { hours, minutes, seconds, _ ->
                        "%02d:%02d:%02d".format(hours, minutes, seconds)
                    }
                    append("⏳ Current Deathtimeout: ", color(128, 128, 128))
                        .append(formattedDeathTimeout, color(255, 153, 0))
                        .newLine()
                    append("🎉 Have fun! 🎉", color(128, 128, 128))
                        .newLine()
                }
                .build()
        }

        fun getQuitIndicator(playerName: String, sessionDurationString: String): Component {
            return ComponentBuilder()
                .append("[", color(128, 128, 128))
                .append("☁", color(139, 0, 0))
                .append("] ", color(128, 128, 128))
                .append(playerName, color(255, 255, 255))
                .append(" (played for $sessionDurationString)")
                .build()
        }

        fun getTablistHeader(): Component {
            return ComponentBuilder()
                .append("☁ ", color(128, 128, 128))
                .append("Smoothcloud ", color(128, 128, 128))
                .append("X-MAS ", color(255, 0, 0))
                .append("Event", color(128, 128, 128))
                .newLine()
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
                .append(Component.text("Quest - $questName", color(255, 215, 0)))
                .append(Component.text(filledSegment, color(0, 255, 0)))
                .append(Component.text(emptySegment, color(255, 0, 0)))
                .append(Component.text(percentText, color(0, 191, 255)))
                .build()
        }

        fun getQuestCompletionMessage(questName: String): Component {
            return Component.text("🎉 You completed the quest: $questName 🎉")
                .color(color(0, 255, 0))
        }

        fun getQuestCompletionAnnounceMessage(playerName: String, questName: String): Component {
            return Component.text("🌟 $playerName has completed the quest: $questName 🌟")
                .color(color(255, 215, 0))
        }

        fun getQuestAfterCompletionText(afterCompletionText: String): Component {
            return Component.text(afterCompletionText)
                .color(color(0, 191, 255)) // Optional: use a distinct color for aftercompletion text
        }

        fun getPlayerDeathAnnounceMessage(playerName: String, formattedJoinDate: String): Component {
            return Component.text("🔥 $playerName has died and was dispelled from the server until $formattedJoinDate 🔥")
                .color(color(255, 0, 0))
        }

        fun getDeathTimerKickMessage(formattedTimeout: String): Component {
            return ComponentBuilder()
                .append("🚫 Due to the rules of 'Huntcraft' 🚫", color(255, 0, 0))
                .newLine()
                .append("You have been dispelled from the server!", color(255, 255, 255))
                .newLine(2)
                .append("You have been dispelled for: ", color(255, 0, 0))
                .append(formattedTimeout, color(255, 0, 0))
                .newLine(2)
                .append("📜 Read more about the rules in our discord 📜", color(115, 138, 219))
                .build()
        }

        fun getDeathTimerTimeoutMessage(formattedDate: String, latestDeath: Long, timeout: Long): Component {
            return ComponentBuilder()
                .append("🚫 You died. 🚫", color(255, 0, 0))
                .newLine()
                .append("You can't rejoin until ", color(255, 255, 255))
                .append(formattedDate, color(255, 215, 0))
                .append(".", color(255, 255, 255))
                .newLine(2)
                .append("⏳ Time until rejoin is possible:", color(255, 255, 255))
                .newLine()
                .append(
                    DateUtil.getFormattedDurationUntilJoin(
                        Instant.now().toEpochMilli(),
                        latestDeath,
                        timeout
                    ), color(124, 252, 0)
                )
                .build()
        }

        fun getQuestListCommandMessage(questName: String, description: String, progression: Int, needed: Int): Component {
            return Component.text()
                .append(Component.text("• ", color(255, 215, 0)))
                .append(Component.text(questName, color(255, 255, 255)))
                .append(Component.text(" - ", color(255, 215, 0)))
                .append(Component.text(description, color(173, 216, 230)))
                .append(Component.text(" (Progress: $progression/$needed)", color(0, 191, 255)))
                .build()
        }
    }
}