package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class SubmitItemEnchantSerializer : JsonSerializer<AdventCalendarSubmitItemEnchantConfig>() {

    override fun serialize(adventCalendarSubmitItemEnchantConfig: AdventCalendarSubmitItemEnchantConfig, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider?) {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("enchant");
        jsonGenerator.writeString(adventCalendarSubmitItemEnchantConfig.enchantment.key.key);
        jsonGenerator.writeFieldName("level");
        jsonGenerator.writeNumber(adventCalendarSubmitItemEnchantConfig.level);
        jsonGenerator.writeEndObject();
    }
}
