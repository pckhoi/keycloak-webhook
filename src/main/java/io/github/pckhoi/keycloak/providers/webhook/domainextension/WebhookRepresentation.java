// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.providers.webhook.domainextension;

import java.util.ArrayList;
import java.util.List;

import io.github.pckhoi.keycloak.providers.webhook.domainextension.jpa.EventFilter;
import io.github.pckhoi.keycloak.providers.webhook.domainextension.jpa.Webhook;

public class WebhookRepresentation {

    private String id;
    private String name;
    private String url;
    private List<EventFilterRepresentation> filters;

    public WebhookRepresentation() {
    }

    public WebhookRepresentation(Webhook webhook) {
        id = webhook.getId();
        name = webhook.getName();
        url = webhook.getURL();
        filters = new ArrayList<EventFilterRepresentation>();
        for (EventFilter filter : webhook.getFilters()) {
            filters.add(new EventFilterRepresentation(filter));
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }

    public List<EventFilterRepresentation> getFilters() {
        return filters;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setFilters(List<EventFilterRepresentation> filters) {
        this.filters = filters;
    }
}
