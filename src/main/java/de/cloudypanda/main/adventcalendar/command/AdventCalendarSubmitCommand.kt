package de.cloudypanda.main.adventcalendar.command

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel
import de.cloudypanda.main.adventcalendar.config.AdventCalendarDayConfig
import de.cloudypanda.main.adventcalendar.config.AdventCalendarSubmitItemConfig
import de.cloudypanda.main.core.integrations.discord.WebhookManager
import de.cloudypanda.main.core.integrations.rest.RequestManager
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean

class AdventCalendarSubmitCommand() : BasicCommand {

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        if (EntityType.PLAYER != commandSourceStack.executor.type) {
            return
        }

        val player = commandSourceStack.executor as Player

        val adventCalendarConfigModel = Huntcraft.instance.adventCalendarConfig;

        if (adventCalendarConfigModel.getConfigForDay(LocalDate.now()) == null) {
            player.sendMessage("There is no challenge for today")
            return
        }

        if (adventCalendarConfigModel.hasPlayerAlreadyCompletedDay(player.uniqueId, LocalDate.now())) {
            player.sendMessage("You have already completed the challenge for today")
            return
        }

        val dayConfig = adventCalendarConfigModel.getConfigForDay(LocalDate.now())
        val itemConfig = dayConfig?.itemToSubmit

        val wasItemSubmitted = AtomicBoolean(false)

        player.inventory.forEach { item ->
            if (item == null) {
                return
            }

            if (validateItemSubmition(item, itemConfig!!)) {
                player.sendMessage("You have successfully submitted the item")

                if (itemConfig.amount != 1) {
                    player.inventory
                        .removeItem(ItemStack(itemConfig.material, itemConfig.amount))
                } else {
                    player.inventory.remove(item)
                }

                completeSuccessfulSubmit(player, adventCalendarConfigModel, dayConfig)
                wasItemSubmitted.set(true)
            }
        }

        if (!wasItemSubmitted.get()) {
            player.sendMessage("No matching item found in your inventory")
        }
    }

    private fun completeSuccessfulSubmit(
        player: Player,
        adventCalendarConfigModel: AdventCalendarConfigModel,
        dayConfig: AdventCalendarDayConfig
    ) {
        adventCalendarConfigModel.setCompletedForPlayer(
            player.uniqueId,
            LocalDate.now(),
            dayConfig.points
        )

        Huntcraft.instance.tablistManager.updatePlayerTablist(player)
        WebhookManager.sendAchievementMessage("${player.displayName()} has completed today's challenge and earned %d points")
        RequestManager().updatePlayerChallenge(player.uniqueId, dayConfig.points)
    }

    private fun validateItemSubmition(item: ItemStack, itemConfig: AdventCalendarSubmitItemConfig): Boolean {
        val validSubmission = AtomicBoolean(true)

        if (item.type != itemConfig.material) {
            validSubmission.set(false)
        }

        if (item.amount < itemConfig.amount) {
            validSubmission.set(false)
        }

        //Only check durability if its configured
        if (itemConfig.durability != null) {
            if (item.itemMeta is Damageable) {

                val damageable = item.itemMeta as Damageable
                if ((item.type.maxDurability - damageable.damage) != itemConfig.durability) {
                    validSubmission.set(false)
                }
            }
        }

        //Only check name if its configured
        if (itemConfig.name != null) {
            if (!item.itemMeta.displayName()?.equals(itemConfig.name)!!) {
                validSubmission.set(false)
            }
        }

        //Only if enchants are configured
        if(itemConfig.enchants != null) {
            if (itemConfig.enchants.isNotEmpty()) {
                itemConfig.enchants.forEach { ench ->
                    if (!item.enchantments
                            .containsKey(ench.enchant) || item.getEnchantmentLevel(ench.enchant) != ench.level
                    ) {
                        validSubmission.set(false)
                    }

                }
            }
        }


        return validSubmission.get()
    }
}
