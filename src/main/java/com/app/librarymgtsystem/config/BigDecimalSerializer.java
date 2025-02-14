package com.app.librarymgtsystem.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // If value is null, write zero
        if (value == null) {
            gen.writeNumber(0); // Default to 0 if null
        } else {
            // Write as plain string to avoid loss of precision
            gen.writeNumber(value.stripTrailingZeros());
        }
    }
}
