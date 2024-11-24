package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;

import java.io.IOException;

public class SubmitItemEnchantDeserializer extends JsonDeserializer<AdventCalendarSubmitItemEnchantConfig> {
    @Override
    public AdventCalendarSubmitItemEnchantConfig deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        AdventCalendarSubmitItemEnchantConfig config = new AdventCalendarSubmitItemEnchantConfig();
        JsonNode node = jsonParser.readValueAsTree();
        config.setEnchant(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(new NamespacedKey("minecraft", node.get("enchant").asText().toLowerCase())));
        config.setLevel(node.get("level").asInt());
        return config;
    }
}
