// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.providers.webhook.events;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = AdminEventSerializer.class)
public class AdminEventRepresentation {
    private String id;
    private AuthDetails authDetails;
    private OperationType operationType;
    private ResourceType resourceType;
    private String representation;
    private String resourcePath;
    private long time;

    public AdminEventRepresentation(AdminEvent event) {
        authDetails = event.getAuthDetails();
        id = event.getId();
        operationType = event.getOperationType();
        resourceType = event.getResourceType();
        representation = event.getRepresentation();
        resourcePath = event.getResourcePath();
        time = event.getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthDetails getAuthDetails() {
        return authDetails;
    }

    public void setAuthDetails(AuthDetails authDetails) {
        this.authDetails = authDetails;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AdminEventRepresentation [authDetails=" + authDetails + ", id=" + id + ", operationType="
                + operationType + ", representation=" + representation + ", resourcePath=" + resourcePath
                + ", resourceType=" + resourceType + ", time=" + time + "]";
    }

}
