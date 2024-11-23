package de.cloudypanda.main.common.file;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarSubmitItemEnchantConfig;

import java.io.IOException;

public class SubmitItemEnchantSerializer extends JsonSerializer<AdventCalendarSubmitItemEnchantConfig> {
    @Override
    public void serialize(AdventCalendarSubmitItemEnchantConfig adventCalendarSubmitItemEnchantConfig, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("enchant");
        jsonGenerator.writeString(adventCalendarSubmitItemEnchantConfig.getEnchant().getKey().getKey());
        jsonGenerator.writeFieldName("level");
        jsonGenerator.writeNumber(adventCalendarSubmitItemEnchantConfig.getLevel());
        jsonGenerator.writeEndObject();
    }
}
