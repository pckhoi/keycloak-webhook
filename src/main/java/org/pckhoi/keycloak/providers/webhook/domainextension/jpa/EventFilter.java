package org.pckhoi.keycloak.providers.webhook.domainextension.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.EnumType;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

@Entity
@Table(name = "EVENT_FILTER")
@NamedQueries({
        @NamedQuery(name = "findByWebhook", query = "from EventFilter as filter inner join filter.webhook as webhook where webhook.id = :webhookId") })
public class EventFilter {

    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "WEBHOOK_ID", nullable = false)
    private Webhook webhook;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "USER_EVENT_TYPE")
    private EventType userEventType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ADMIN_EVENT_OPERATION_TYPE")
    private OperationType adminEventOperationType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ADMIN_EVENT_RESOURCE_TYPE")
    private ResourceType adminEventResourceType;

    public String getId() {
        return id;
    }

    public Webhook getWebhook() {
        return webhook;
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

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
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
