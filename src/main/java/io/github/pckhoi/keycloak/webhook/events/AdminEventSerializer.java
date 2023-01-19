// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.events;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * AdminEventSerializer serializes all non-null fields of
 * AdminEventRepresentation
 */
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
        if (value.getId() != null) {
            jgen.writeStringField("id", value.getId());
        }
        if (value.getAuthDetails() != null) {
            jgen.writeObjectField("authDetails", value.getAuthDetails());
        }
        if (value.getOperationType() != null) {
            jgen.writeStringField("operationType", value.getOperationType().toString());
        }
        if (value.getResourceType() != null) {
            jgen.writeStringField("resourceType", value.getResourceType().toString());
        }
        if (value.getResourcePath() != null) {
            jgen.writeStringField("resourcePath", value.getResourcePath());
        }
        jgen.writeNumberField("time", value.getTime());
        if (value.getRepresentation() != null) {
            jgen.writeFieldName("representation");
            jgen.writeRawValue(value.getRepresentation());
        }
        jgen.writeEndObject();
    }
}
