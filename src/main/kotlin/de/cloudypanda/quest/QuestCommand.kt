package de.cloudypanda.quest

import de.cloudypanda.player.PlayerManager
import de.cloudypanda.util.TextUtil
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class QuestCommand() : BasicCommand {
    override fun execute(
        commandSourceStack: CommandSourceStack,
        args: Array<out String>
    ) {
        val sender: CommandSender? = commandSourceStack.sender

        if (sender !is Player) {
            return
        }

        val player = PlayerManager.getPlayerByUUID(sender.uniqueId) ?: return

        // Ongoing Quests Header
        sender.sendMessage(Component.text("Quests").color(TextColor.color(0, 255, 0)))
        player.ongoingQuests.forEach { quest ->
            sender.sendMessage(TextUtil.getOngoingQuestListCommandMessage(quest.name, quest.description, quest.progression,quest.requiredAmount))
        }

        // Completed Quests Header
        player.finishedQuests.sortByDescending { it.completedOn }
        player.finishedQuests.forEach { quest ->
            sender.sendMessage(TextUtil.getCompletedQuestListCommandMessage(quest.name, quest.description, quest.completionState, quest.completedOn))
        }
    }
}