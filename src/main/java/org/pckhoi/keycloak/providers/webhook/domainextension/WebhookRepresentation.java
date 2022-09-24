/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pckhoi.keycloak.providers.webhook.domainextension;

import java.util.ArrayList;
import java.util.List;

import org.pckhoi.keycloak.providers.webhook.domainextension.jpa.EventFilter;
import org.pckhoi.keycloak.providers.webhook.domainextension.jpa.Webhook;

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
