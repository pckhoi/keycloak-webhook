package org.wrangle.keycloak.providers.webhook.domainextension;

import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.wrangle.keycloak.providers.webhook.domainextension.jpa.EventFilter;

public class EventFilterRepresentation {

    private String id;
    private EventType userEventType;
    private OperationType adminEventOperationType;
    private ResourceType adminEventResourceType;

    public EventFilterRepresentation() {
    }

    public EventFilterRepresentation(EventFilter filter) {
        id = filter.getId();
        userEventType = filter.getUserEventType();
        adminEventOperationType = filter.getAdminEventOperationType();
        adminEventResourceType = filter.getAdminEventResourceType();
    }

    public String getId() {
        return id;
    }

    public EventType getUserEventType() {
        return userEventType;
    }

    public OperationType getAdminEventOperationType() {
        return adminEventOperationType;
    }

    public ResourceType getAdminEventResourceType() {
        return adminEventResourceType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserEventType(EventType userEventType) {
        this.userEventType = userEventType;
    }

    public void setAdminEventOperationType(OperationType adminEventOperationType) {
        this.adminEventOperationType = adminEventOperationType;
    }

    public void setAdminEventResourceType(ResourceType adminEventResourceType) {
        this.adminEventResourceType = adminEventResourceType;
    }

}
