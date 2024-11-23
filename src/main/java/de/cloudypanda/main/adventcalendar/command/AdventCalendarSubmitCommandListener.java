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

        AdventCalendarDayConfig dayConfig = adventCalendarConfigModel.getConfigForDay(LocalDate.now());
        AdventCalendarSubmitItemConfig itemConfig = dayConfig.getItemToSubmit();

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
            }
        });
    }

    private boolean validateItemSubmition(ItemStack item, AdventCalendarSubmitItemConfig itemConfig) {
        boolean validSubmission = true;

        if(item.getType() != itemConfig.getMaterial()) {
            validSubmission = false;
        }

        if(item.getAmount() < itemConfig.getAmount()) {
            validSubmission = false;
        }

        if(itemConfig.getDurability() != null && item.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) item.getItemMeta();

            if((item.getType().getMaxDurability() - damageable.getDamage()) != itemConfig.getDurability()) {
                validSubmission = false;
            }
        }

        return validSubmission;
    }
}
