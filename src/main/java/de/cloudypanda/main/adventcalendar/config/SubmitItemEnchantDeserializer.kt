package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.w3c.dom.Node

class SubmitItemEnchantDeserializer : JsonDeserializer<AdventCalendarSubmitItemEnchantConfig>() {
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): AdventCalendarSubmitItemEnchantConfig {
        val node = jsonParser.readValuesAs(TreeNode::class.java) as TreeNode

        val enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey("minecraft", node.get("enchant").asToken().asString().lowercase()))!!;
        val level = node.get("level").asToken().asString().toInt();

        return AdventCalendarSubmitItemEnchantConfig(enchant, level);
    }
}
