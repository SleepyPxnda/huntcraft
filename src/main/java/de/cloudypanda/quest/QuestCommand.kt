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

        val player = PlayerManager.instance.getPlayerByUUID(sender.uniqueId) ?: return

        // Ongoing Quests Header
        sender.sendMessage(Component.text("Ongoing Quests:").color(TextColor.color(0, 255, 0)))
        player.ongoingQuests.forEach { quest ->
            sender.sendMessage(TextUtil.getQuestListCommandMessage(quest.name, quest.progression, quest.requiredAmount))
        }

        // Completed Quests Header
        sender.sendMessage(Component.text("Completed Quests:").color(TextColor.color(255, 215, 0)))
        player.finishedQuests.forEach { quest ->
            sender.sendMessage(Component.text()
                .append(Component.text("• ", TextColor.color(255, 215, 0)))
                .append(Component.text(quest.name, TextColor.color(192, 192, 192)))
                .build())
        }
    }
}