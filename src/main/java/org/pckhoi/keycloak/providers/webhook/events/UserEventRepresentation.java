package org.pckhoi.keycloak.providers.webhook.events;

import java.util.Map;

import org.keycloak.events.Event;
import org.keycloak.events.EventType;

public class UserEventRepresentation {
    private Map<String, String> details;
    private long time;
    private EventType type;
    private String userId;
    private String clientId;

    public UserEventRepresentation(Event event) {
        details = event.getDetails();
        time = event.getTime();
        type = event.getType();
        userId = event.getUserId();
        clientId = event.getClientId();
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
