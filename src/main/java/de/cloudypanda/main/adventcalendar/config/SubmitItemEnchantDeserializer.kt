package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey

class SubmitItemEnchantDeserializer : JsonDeserializer<AdventCalendarSubmitItemEnchantConfig>() {
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): AdventCalendarSubmitItemEnchantConfig {

        val tree = jsonParser.readValueAsTree<TreeNode>()

        val enchant = tree.get("enchant").toString().replace("\"", "");
        val parsedEnchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
            .get(NamespacedKey("minecraft", enchant.lowercase()))!!;

        val levelToken = tree.get("level").toString().replace("\"", "");
        val level = levelToken.toInt();

        return AdventCalendarSubmitItemEnchantConfig(parsedEnchant, level);
    }
}
