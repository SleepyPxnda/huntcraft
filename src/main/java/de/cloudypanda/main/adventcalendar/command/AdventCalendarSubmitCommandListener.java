package de.cloudypanda.main.adventcalendar.command;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarDayConfig;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarSubmitItemConfig;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdventCalendarSubmitCommandListener implements BasicCommand {

    private final Huntcraft huntcraft;

    public AdventCalendarSubmitCommandListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if(!EntityType.PLAYER.equals(commandSourceStack.getExecutor().getType())) {
            return;
        }

        Player player = (Player) commandSourceStack.getExecutor();

        AdventCalendarConfigModel adventCalendarConfigModel = huntcraft.adventCalendarConfigManager.readFromFile();

        if(adventCalendarConfigModel.getConfigForDay(LocalDate.now()) == null){
            player.sendMessage("There is no challenge for today");
            return;
        }

        if(adventCalendarConfigModel.hasPlayerAlreadyCompletedDay(player.getUniqueId(), LocalDate.now())) {
            player.sendMessage("You have already completed the challenge for today");
            return;
        }

        AdventCalendarDayConfig dayConfig = adventCalendarConfigModel.getConfigForDay(LocalDate.now());
        AdventCalendarSubmitItemConfig itemConfig = dayConfig.getItemToSubmit();

        AtomicBoolean wasItemSubmitted = new AtomicBoolean(false);

        player.getInventory().forEach(item -> {
            if(item == null){
                return;
            }

            if(validateItemSubmition(item, itemConfig)) {
                player.sendMessage("You have successfully submitted the item");

                if(itemConfig.getAmount() != 1){
                    player.getInventory().removeItem(new ItemStack(itemConfig.getMaterial(), itemConfig.getAmount()));
                } else {
                    player.getInventory().remove(item);
                }

                wasItemSubmitted.set(true);
                adventCalendarConfigModel.setCompletedForPlayer(player.getUniqueId(), LocalDate.now(), dayConfig.getPoints());
                huntcraft.adventCalendarConfigManager.saveToFile(adventCalendarConfigModel);
            }
        });

        if(!wasItemSubmitted.get()) {
            player.sendMessage("No matching item found in your inventory");
        }
    }

    private boolean validateItemSubmition(ItemStack item, AdventCalendarSubmitItemConfig itemConfig) {
        AtomicBoolean validSubmission = new AtomicBoolean(true);

        if(item.getType() != itemConfig.getMaterial()) {
            validSubmission.set(false);
        }

        if(item.getAmount() < itemConfig.getAmount()) {
            validSubmission.set(false);
        }

        //Only check durability if its configured
        if(itemConfig.getDurability() != null ) {
            if(item.getItemMeta() instanceof Damageable damageable) {

                if((item.getType().getMaxDurability() - damageable.getDamage()) != itemConfig.getDurability()) {
                    validSubmission.set(false);
                }
            }
        }

        //Only check name if its configured
        if(itemConfig.getName() != null) {
            if(!item.getItemMeta().displayName().equals(itemConfig.getName())) {
                validSubmission.set(false);
            }
        }

        //Only if enchants are configured
        if(itemConfig.getEnchants() != null && !itemConfig.getEnchants().isEmpty()) {
            itemConfig.getEnchants().forEach(ench -> {
                if(!item.getEnchantments().containsKey(ench.getEnchant()) || item.getEnchantmentLevel(ench.getEnchant()) != ench.getLevel()) {
                    validSubmission.set(false);
                }
            });
        }

        return validSubmission.get();
    }
}
