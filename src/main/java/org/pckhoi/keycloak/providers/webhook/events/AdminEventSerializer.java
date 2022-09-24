package org.pckhoi.keycloak.providers.webhook.events;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class AdminEventSerializer extends StdSerializer<AdminEventRepresentation> {

    public AdminEventSerializer() {
        this(null);
    }

    public AdminEventSerializer(Class<AdminEventRepresentation> t) {
        super(t);
    }

    @Override
    public void serialize(
            AdminEventRepresentation value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId());
        jgen.writePOJOField("authDetails", value.getAuthDetails());
        jgen.writeStringField("operationType", value.getOperationType().toString());
        jgen.writeStringField("resourceType", value.getResourceType().toString());
        jgen.writeStringField("resourcePath", value.getResourcePath());
        jgen.writeNumberField("time", value.getTime());
        jgen.writeFieldName("representation");
        jgen.writeRawValue(value.getRepresentation());
        jgen.writeEndObject();
    }
}
