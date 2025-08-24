package de.cloudypanda.main.quest

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
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

        val questsForPlayer = QuestSystem.getCurrentQuestsForPlayer(sender.uniqueId)
        sender.sendMessage("You have ${questsForPlayer.size} ongoing quests:")
        for (quest in questsForPlayer) {
            sender.sendMessage(
                "- ${quest.name}: (Progress: ${quest.progression}/${
                    when (quest.type) {
                        QuestType.BLOCK_BREAK -> quest.requiredBlockBreakCount
                        QuestType.BLOCK_PLACE -> quest.requiredBlockPlaceCount
                        QuestType.ENTITY_KILL -> quest.requiredEntityKillCount
                        QuestType.ITEM_CRAFT -> quest.requiredItemCraftCount
                        QuestType.TURN_IN_ITEM -> quest.requiredTurnInItemCount
                        QuestType.PUZZLE_COMPLETE -> 1
                        QuestType.ACHIEVEMENT -> 1
                        else -> 0
                    }
                })"
            )
        }

        val completedQuests: List<QuestDefinition> = QuestSystem.getCompletedQuestsForPlayer(sender.uniqueId)
        sender.sendMessage("You have ${completedQuests.size} completed quests:")
        for (quest in completedQuests) {
            sender.sendMessage("- ${quest.name} ")
        }
    }
}