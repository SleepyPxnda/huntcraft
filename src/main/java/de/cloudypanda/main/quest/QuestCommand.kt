package de.cloudypanda.main.quest

import de.cloudypanda.main.util.TextUtil
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
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
        val completedQuests: List<QuestDefinition> = QuestSystem.getCompletedQuestsForPlayer(sender.uniqueId)

        // Ongoing Quests Header
        sender.sendMessage(Component.text("Ongoing Quests:").color(net.kyori.adventure.text.format.TextColor.color(0, 255, 0)))
        questsForPlayer.forEach { quest ->
            sender.sendMessage(TextUtil.getQuestProgressBar(quest.name, quest.progression, quest.getNeededCount()))
            sender.sendMessage(TextUtil.getQuestListCommandMessage(quest.name, quest.description, quest.progression, quest.getNeededCount()))
        }

        // Completed Quests Header
        sender.sendMessage(Component.text("Completed Quests:").color(net.kyori.adventure.text.format.TextColor.color(255, 215, 0)))
        completedQuests.forEach { quest ->
            sender.sendMessage(Component.text()
                .append(Component.text("• ", net.kyori.adventure.text.format.TextColor.color(255, 215, 0)))
                .append(Component.text(quest.name, net.kyori.adventure.text.format.TextColor.color(192, 192, 192)))
                .build())
        }
    }
}